package com.hongguo.tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hongguo.tv.CatalogResponse
import com.hongguo.tv.PlayerViewModel

@Composable
fun HongguoTvApp(
    viewModel: PlayerViewModel = viewModel()
) {
    val catalog by viewModel.catalog.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCatalog()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0E14)),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "红果开源播放器 TV",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { viewModel.loadCatalog() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1F9DFF),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("刷新")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "精选 / 分类 / 最近播放",
                color = Color(0xFFBFC7D5),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            TvCatalogList(catalog)
        }
    }
}

@Composable
private fun TvCatalogList(catalog: CatalogResponse) {
    val items = catalog.items.ifEmpty {
        listOf(
            "未连接到服务",
            "请确认宿主机运行了 src/server",
            "并使用 10.0.2.2:8899 或真实 IP"
        )
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        items(items) { title ->
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(150.dp)
                    .background(
                        color = Color(0xFF171E2D),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
