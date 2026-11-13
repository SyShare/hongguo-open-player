package com.hongguo.tv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF1F9DFF),
    secondary = androidx.compose.ui.graphics.Color(0xFF9C6BFF),
    tertiary = androidx.compose.ui.graphics.Color(0xFFFFC857),
    background = androidx.compose.ui.graphics.Color(0xFF0B0E14),
    surface = androidx.compose.ui.graphics.Color(0xFF131922),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color.White
)

@Composable
fun HongguoTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
