package com.example.money_manage_app.features.common.theme

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// 🎨 Màu sáng
private val LightColorScheme = lightColorScheme(
    primary = YellowPrimary,
    secondary = YellowPrimary,
    background = YellowBackground,
    surface = YellowBackground,
    onPrimary = TextBlack,
    onSecondary = TextBlack,
    onBackground = TextBlack,
    onSurface = TextBlack
)

// 🌙 Màu tối
private val DarkColorScheme = darkColorScheme(
    primary = YellowPrimary,
    secondary = YellowPrimary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun SettingPartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current  // ✅ Lấy context tại đây
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !darkTheme

    // 🧱 Cập nhật màu thanh trạng thái và điều hướng
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = colorScheme.background,
            darkIcons = useDarkIcons
        )
        systemUiController.setNavigationBarColor(
            color = colorScheme.surface,
            darkIcons = useDarkIcons
        )
    }

    // 🎨 Áp dụng theme typography động
    DynamicTypographyTheme {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = MaterialTheme.shapes,
            content = content
        )
    }
}
