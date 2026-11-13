// SPDX-License-Identifier: GPL-3.0-only
const express = require('express');
const http = require('node:http');
const path = require('path');
const { randomBytes } = require('node:crypto');
const sessionToken = randomBytes(32).toString('hex');
const dramaService = require('./services/dramaService');
const streamService = require('./services/streamService');
const storeService = require('./services/storeService');
const logger = require('./services/logger');

const app = express();
const PORT = process.env.PORT || 8899;
const HOST = process.env.HONGGUO_HOST || '127.0.0.1';
const ALLOW_LAN_ACCESS = process.env.HONGGUO_ALLOW_LAN === '1' || process.env.HONGGUO_TV_MODE === '1';

function isLocalOrPrivateHost(hostname) {
  if (!hostname) return false;
  const value = String(hostname).toLowerCase().trim();
  if (['localhost', '127.0.0.1', '::1', '[::1]', '0.0.0.0'].includes(value)) return true;
  if (/^10\./.test(value) || /^192\.168\./.test(value) || /^172\.(1[6-9]|2\d|3[0-1])\./.test(value)) return true;
  return false;
}

app.use((req, res, next) => {
  const host = req.headers.host || '';
  const incomingHost = host.split(':')[0];
  const allowBypass = ALLOW_LAN_ACCESS && isLocalOrPrivateHost(incomingHost);
  if (allowBypass) {
    return next();
  }
  res.set({
    'X-Content-Type-Options': 'nosniff',
    'X-Frame-Options': 'DENY',
    'Referrer-Policy': 'no-referrer',
    // Vue's local template compiler currently requires unsafe-eval.
    'Content-Security-Policy': "default-src 'none'; script-src 'self' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; media-src 'self' blob:; connect-src 'self'; font-src 'self' data:; object-src 'none'; base-uri 'none'; frame-ancestors 'none';"
  });
  next();
});

// Only the local application's own origin may access personal data or trigger downloads.
app.use((req, res, next) => {
  const host = String(req.headers.host || '').split(':')[0];
  const allowLan = ALLOW_LAN_ACCESS && isLocalOrPrivateHost(host);
  const isHostAllowed = allowLan || [`127.0.0.1`, 'localhost', '::1', '[::1]'].includes(host);
  if (!isHostAllowed ||
      (req.headers.origin && req.headers.origin !== `http://${req.headers.host}` && !allowLan) ||
      (req.headers.origin && req.headers.origin !== `http://${host}` && allowLan) ||
      ['cross-site', 'same-site'].includes(req.headers['sec-fetch-site'])) {
    if (ALLOW_LAN_ACCESS && isLocalOrPrivateHost(host)) {
      return next();
    }
    return res.status(403).json({ error: '仅允许本地播放器访问' });
  }
  next();
});
app.use((req, res, next) => {
  const normalizedPath = req.path.toLowerCase();
  const host = String(req.headers.host || '').split(':')[0];
  const port = req.socket?.localPort;
  const portCookieName = port ? `player_session_${port}` : null;
  const allowLanSession = ALLOW_LAN_ACCESS && isLocalOrPrivateHost(host);
  if (normalizedPath.startsWith('/api/')) {
    if (allowLanSession) {
      return next();
    }
    const cookies = String(req.headers.cookie || '').split(';').map(value => value.trim());
    const hasValidSession = (portCookieName && cookies.includes(`${portCookieName}=${sessionToken}`)) ||
                            cookies.includes(`player_session=${sessionToken}`);
    if (!hasValidSession) return res.status(401).json({ error: '请从播放器首页建立本地会话' });
  } else if (normalizedPath === '/' || normalizedPath === '/index.html') {
    if (portCookieName) {
      res.cookie(portCookieName, sessionToken, { httpOnly: true, sameSite: 'strict', path: '/' });
    }
    res.cookie('player_session', sessionToken, { httpOnly: true, sameSite: 'strict', path: '/' });
    res.set('Cache-Control', 'no-store');
  }
  next();
});
app.use(express.json({ limit: '64kb' }));

// 核心 API 请求访问与耗时日志中间件 (用于日常排错与运行诊断)
app.use((req, res, next) => {
  if (req.path.toLowerCase().startsWith('/api/')) {
    const t0 = Date.now();
    res.on('finish', () => {
      const duration = Date.now() - t0;
      const msg = `${req.method} ${req.originalUrl || req.url} -> ${res.statusCode} (${duration}ms)`;
      if (res.statusCode >= 500) {
        logger.error('API', msg);
      } else if (res.statusCode >= 400 && res.getHeader('X-Stream-Mode') !== 'bytevc2-progressive') {
        logger.warn('API', msg);
      } else {
        logger.info('API', msg);
      }
    });
  }
  next();
});

// 静态前端资源目录
const clientDir = path.join(__dirname, '../client');
app.use(express.static(clientDir));

function sendApiError(res, error) {
  const status = error.status >= 400 && error.status < 600 ? error.status : 500;
  const messages = {
    DEVICE_REGISTRATION_FAILED: '播放设备注册暂时失败，请稍后重试',
    ETIMEDOUT: '上游请求超时，请稍后重试',
    SECURITY_BLOCKED: '视频源地址未通过安全检查',
    EACCES: '无法访问本地数据目录，请检查写入权限',
    EPERM: '无法保存本地数据，请稍后重试',
    EBUSY: '本地数据文件正被占用，请稍后重试'
  };
  logger.error('API', `${error.code || 'REQUEST_FAILED'} - ${error.message}`);
  res.status(status).json({ error: status === 400 ? error.message : messages[error.code] || '操作失败，请稍后重试' });
}

app.get('/api/system/version', (_req, res) => {
  res.set('Cache-Control', 'no-store');
  res.json({ version: require('../../package.json').version, host: HOST, lanMode: ALLOW_LAN_ACCESS });
});

app.get('/api/danmaku/:seriesId/:vid', async (req, res) => {
  const { seriesId, vid } = req.params;
  if (!/^\d{6,30}$/.test(seriesId) || !/^\d{6,30}$/.test(vid)) return res.status(400).json({ error: '剧集编号无效' });
  const result = await require('./native/danmaku').fetchDanmaku({ seriesId, vid, fresh: req.query.fresh === '1' });
  res.set('Cache-Control', 'no-store');
  res.json(result);
});

// ... rest of file unchanged ...

async function startServer(preferredPort = PORT) {
  function listen(port) {
    return new Promise((resolve, reject) => {
      const server = http.createServer(app);
      const onError = error => {
        server.removeListener('listening', onListening);
        reject(error);
      };
      const onListening = () => {
        server.removeListener('error', onError);
        resolve(server);
      };
      server.once('error', onError);
      server.once('listening', onListening);
      server.listen(port, HOST);
    });
  }

  let server;
  try {
    server = await listen(preferredPort);
  } catch (error) {
    if (error.code !== 'EADDRINUSE') throw error;
    logger.warn('Server', `端口 ${preferredPort} 已被占用，尝试使用动态空闲端口...`);
    server = await listen(0);
  }
  const actualPort = server.address().port;
  const actualHost = HOST === '0.0.0.0' ? 'LAN' : HOST;
  logger.info('Server', `🚀 红果开源播放器服务已启动: http://${actualHost}:${actualPort}`);
  logger.info('Server', `📄 运行日志文件: ${logger.getLogFilePath()}`);
  return { app, server, port: actualPort, host: HOST };
}

if (require.main === module) {
  startServer().then(({ port, host }) => {
    if (process.argv.includes('--open') || process.env.HONGGUO_AUTO_OPEN === '1') {
      const url = `http://${host === '0.0.0.0' ? '127.0.0.1' : host}:${port}`;
      const cmd = process.platform === 'darwin' ? `open "${url}"` :
                  process.platform === 'win32' ? `start "" "${url}"` :
                  `xdg-open "${url}"`;
      require('child_process').exec(cmd, (err) => {
        if (err) logger.warn('Server', `无法自动打开浏览器: ${err.message}`);
      });
    }
  }).catch((err) => {
    console.error('启动服务失败:', err);
    process.exit(1);
  });
}

module.exports = { app, startServer };
