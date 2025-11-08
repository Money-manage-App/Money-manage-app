package com.example.money_manage_app.features.common.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.money_manage_app.data.local.datastore.*
@Composable
fun DynamicTypographyTheme(
    content: @Composable () -> Unit
) {
    // Lấy context từ Compose
    val context = LocalContext.current

    // Khởi tạo FontSizeManager với context đúng package
    val fontSizeManager = remember { FontSizeManager(context) }
    val fontScale by fontSizeManager.fontSizeFlow.collectAsState(initial = 1f)

    // Tạo typography động
    val dynamicTypography = Typography(
        bodyLarge = Typography().bodyLarge.copy(fontSize = 16.sp * fontScale),
        bodyMedium = Typography().bodyMedium.copy(fontSize = 14.sp * fontScale),
        bodySmall = Typography().bodySmall.copy(fontSize = 12.sp * fontScale),
        titleLarge = Typography().titleLarge.copy(fontSize = 20.sp * fontScale),
        titleMedium = Typography().titleMedium.copy(fontSize = 18.sp * fontScale),
        titleSmall = Typography().titleSmall.copy(fontSize = 16.sp * fontScale),
        labelLarge = Typography().labelLarge.copy(fontSize = 14.sp * fontScale)
    )

    MaterialTheme(
        typography = dynamicTypography,
        content = content
    )
}
