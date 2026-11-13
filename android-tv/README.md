# 红果开源播放器 Android TV 适配工程

本目录是基于现有仓库的 Android TV 适配骨架，目标是保留现有 `src/server` 中的媒体服务能力，并新增一个 TV 端客户端，使用遥控器交互和大屏 UI。

## 设计原则

- 不直接改造 Electron 桌面端代码
- 复用现有 `src/server` 的 API 能力：搜索、详情、播放信息、收藏、历史、播放代理等
- Android TV 端负责：大屏布局、遥控器导航、播放控制、用户数据管理
- 统一使用 `http://127.0.0.1:8899` 或可配置 API server 访问已有服务

## 已完成的适配骨架

- Android TV Gradle 项目结构
- Leanback / Compose 入口页面
- TV 端 API 接口抽象
- 首页、详情、播放页面的示例组件骨架

## 下一步执行顺序

1. 将 `src/server` 相关接口抽离为标准 API contract
2. 在 `android-tv/app` 中补齐 ViewModel、Repository 层
3. 接入 ExoPlayer 播放器，并绑定 `/api/play/:vid`、`/api/stream/:vid`
4. 补齐搜索、详情、收藏、历史、分集切换
5. 适配 D-pad、返回键、菜单键、快进快退键
6. 压测遥控器聚焦和电视端 UI 兼容性

## 目录结构

- `android-tv/`：Android TV 项目
- `src/server/`：保留原有媒体能力，不改动主逻辑

## 运行方式

```bash
cd android-tv
./gradlew assembleDebug
```

## 端口约定

默认保留现有服务端口：

- 8899：本地播放器服务

如果需要从 Android TV 真机访问 PC 主机服务，请在 `ApiConfig.kt` 中改成目标宿主 IP，而不是固定 127.0.0.1。

## 注意事项

- Android TV 设备不能直接用现有 Electron 窗口；需要单独使用 Android 客户端。
- 需要单独处理 10-foot UI 动效与遥控器焦点逻辑。
- 播放地址与流媒体代理能力保持复用，不在 TV App 中重新实现解密逻辑。

## 目标成品

首版目标：

- 首页推荐
- 分类列表
- 搜索
- 详情页
- 播放页
- 历史/收藏
- 返回和导航逻辑

这是一个 TV 版本第一版的清晰落地起点。