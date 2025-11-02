package com.example.money_manage_app.features.profile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// ✅ Đúng import
import com.example.money_manage_app.features.settings.data.LanguagePreference

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    // 🌍 Lấy ngôn ngữ hiện tại từ DataStore
    val languagePref = remember { LanguagePreference(context) }
    val currentLanguage: String by languagePref.currentLanguage.collectAsState(initial = "Tiếng Việt")

    val brandYellow = if (isDark) Color(0xFFFBC02D) else Color(0xFFFEE912)
    val textColor = if (isDark) Color.White else Color.Black

    SideEffect {
        systemUiController.setStatusBarColor(
            color = brandYellow,
            darkIcons = !isDark
        )
    }

    // 🗣️ Text hiển thị theo ngôn ngữ
    val loginText = if (currentLanguage == "English") "Sign In" else "Đăng nhập"
    val loginSubText = if (currentLanguage == "English") "Sign in for more features!" else "Đăng nhập, thú vị hơn!"
    val settingText = if (currentLanguage == "English") "Settings" else "Cài đặt"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // 🟡 Header vàng
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(brandYellow)
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = loginText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = loginSubText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textColor.copy(alpha = 0.9f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⚙️ Thẻ Cài đặt
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { navController.navigate("settings") },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = brandYellow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = settingText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
