package com.example.money_manage_app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.money_manage_app.features.common.theme.SettingPartTheme
import com.example.money_manage_app.features.common.utils.LocaleHelper
import com.example.money_manage_app.features.settings.data.LanguagePreference
import com.example.money_manage_app.features.settings.screens.data.ThemePreference
import com.example.money_manage_app.features.ui.MainScreen
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val languagePref = LanguagePreference(newBase)
        val savedLanguage = runBlocking { languagePref.currentLanguage.first() }
        val context = LocaleHelper.setLocale(newBase, savedLanguage)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            val themePref = ThemePreference(this)
            val isDark by themePref.isDarkMode.collectAsState(initial = false)
            val navController = rememberNavController()

            SettingPartTheme(darkTheme = isDark) {
                NavHost(
                    navController = navController,
                    startDestination = "main" // 🚀 Mở app vào luôn MainScreen
                ) {
                    composable("main") {
                        MainScreen()
                    }
                }
            }
        }
    }
}
