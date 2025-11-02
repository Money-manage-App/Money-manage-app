package com.example.money_manage_app.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.money_manage_app.features.home.screens.HomeScreen
import com.example.money_manage_app.features.history.screens.HistoryScreen
import com.example.money_manage_app.features.report.screens.ReportScreen
import com.example.money_manage_app.features.profile.screens.ProfileScreen
import com.example.money_manage_app.features.settings.screens.settings.SettingsScreen
import com.example.money_manage_app.features.settings.screens.userprofile.UserProfileScreen
import com.example.money_manage_app.features.settings.screens.userprofile.EditProfileScreen
import com.example.money_manage_app.features.settings.screens.settings.ThemeSettingScreen
import com.example.money_manage_app.features.settings.screens.settings.LanguageSettingScreen
import com.example.money_manage_app.features.settings.screens.font.FontSizeScreen

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
    }
}

object Routes {
    const val Home = "home"
    const val History = "history"
    const val Report = "report"
    const val Profile = "profile"
    const val Settings = "settings"
    const val UserProfile = "user_profile"
    const val EditProfile = "edit_profile"
    const val ThemeSettings = "theme_settings"
    const val LanguageSettings = "language_settings"
    const val FontSizeSettings = "font_settings"
}
