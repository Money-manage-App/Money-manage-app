package com.example.money_manage_app.features.ui.screens.settings.settings

import androidx.compose.ui.unit.dp
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

// ✅ CHỈNH LẠI IMPORT CHO ĐÚNG
import com.example.money_manage_app.features.common.utils.LocaleHelper
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val pref = remember { LanguagePreference(context) }
    val scope = rememberCoroutineScope()
    val lang by pref.currentLanguage.collectAsState(initial = "Tiếng Việt")

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (lang == "English") "Language" else "Ngôn ngữ",
                        color = colors.onPrimary,
                        style = typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Danh sách ngôn ngữ
            listOf("English", "Tiếng Việt").forEach { item ->
                val isSelected = lang == item

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 6.dp)
                        .clickable {
                            scope.launch {
                                pref.setLanguage(item)
                                val activity = context as? Activity
                                activity?.let {
                                    LocaleHelper.updateLocale(it, item)
                                }
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            colors.secondaryContainer
                        else colors.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 🔹 Dùng typography động thay vì fontSize cố định
                        Text(
                            text = item,
                            style = typography.bodyLarge.copy(
                                color = colors.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
