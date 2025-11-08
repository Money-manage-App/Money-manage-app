package com.example.money_manage_app.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.money_manage_app.features.ui.screens.home.HomeScreen
import com.example.money_manage_app.features.ui.screens.history.HistoryScreen
import com.example.money_manage_app.features.ui.screens.report.ReportScreen
import com.example.money_manage_app.features.ui.screens.profile.ProfileScreen
import com.example.money_manage_app.features.ui.screens.settings.settings.SettingsScreen
import com.example.money_manage_app.features.ui.screens.settings.userprofile.UserProfileScreen
import com.example.money_manage_app.features.ui.screens.settings.userprofile.EditProfileScreen
import com.example.money_manage_app.features.ui.screens.settings.settings.ThemeSettingScreen
import com.example.money_manage_app.features.ui.screens.settings.settings.LanguageSettingScreen
import com.example.money_manage_app.features.ui.screens.settings.font.FontSizeScreen
import com.example.money_manage_app.features.ui.screens.add.*
import com.example.money_manage_app.features.ui.screens.settings.settings.CategorySettingScreen
import com.example.money_manage_app.features.ui.screens.settings.settings.CurrencySettingScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startAtSettings: Boolean = false
) {
    val startDestination = if (startAtSettings) Routes.Settings else Routes.Home

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Home) { HomeScreen(navController) }
        composable(Routes.History) { HistoryScreen(navController) }
        composable(Routes.Add) { AddScreen(navController) }
        composable(Routes.Report) { ReportScreen(navController) }
        composable(Routes.Profile) { ProfileScreen(navController) }
        composable(Routes.Settings) { SettingsScreen(navController) }

        // Hồ sơ người dùng
        composable(Routes.UserProfile) { UserProfileScreen(navController) }
        composable(Routes.EditProfile) { EditProfileScreen(navController) }

        // Cài đặt
        composable(Routes.ThemeSettings) { ThemeSettingScreen(navController) }
        composable(Routes.LanguageSettings) { LanguageSettingScreen(navController) }
        composable(Routes.FontSizeSettings) { FontSizeScreen(navController) }

        // Thêm 2 màn hình mới
        composable(Routes.CategorySettings) { CategorySettingScreen(navController) }
        composable(Routes.CurrencySettings) { CurrencySettingScreen(navController) }
    }
}

object Routes {
    const val Home = "home"
    const val History = "history"
    const val Report = "report"
    const val Add = "add"
    const val Profile = "profile"
    const val Settings = "settings"
    const val UserProfile = "user_profile"
    const val EditProfile = "edit_profile"
    const val ThemeSettings = "theme_settings"
    const val LanguageSettings = "language_settings"
    const val FontSizeSettings = "font_settings"
    const val CategorySettings = "category_settings"
    const val CurrencySettings = "currency_settings"
}
