package com.hongguo.tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hongguo.tv.PlayerViewModel

@Composable
fun HongguoTvApp(
    viewModel: PlayerViewModel = viewModel()
) {
    viewModel.loadCatalog()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "红果开源播放器 TV",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Android TV 适配中：首页 / 分类 / 搜索 / 详情 / 播放页",
                color = Color(0xFFB8B8B8),
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "服务端接口已保留在 src/server，TV 端通过 API 接口访问媒体能力",
                color = Color(0xFF7D7D7D),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
