package com.hongguo.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
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
                text = "Android TV 适配骨架 - 首页 / 分类 / 搜索 / 播放页入口",
                color = Color(0xFFBDBDBD),
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "后续接入服务：/api/catalog /api/search /api/series/:id /api/play/:vid /api/stream/:vid",
                color = Color(0xFF8E8E93),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }
}
