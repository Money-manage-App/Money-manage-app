package com.example.money_manage_app.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.money_manage_app.MyApp
import com.example.money_manage_app.data.repository.UserRepository

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
import com.example.money_manage_app.features.ui.screens.settings.settings.AddExpenseCategoryScreen
import com.example.money_manage_app.features.ui.screens.settings.settings.AddIncomeCategoryScreen
import com.example.money_manage_app.features.ui.screens.settings.settings.CurrencySettingScreen

// Import màn hình chi tiết giao dịch
import com.example.money_manage_app.features.ui.screens.history.TransactionDetailScreen
import com.example.money_manage_app.features.viewmodel.UserViewModel
import com.example.money_manage_app.features.viewmodel.UserViewModelFactory

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startAtSettings: Boolean = false
) {
    val startDestination = if (startAtSettings) Routes.Settings else Routes.Home
    val userRepository = UserRepository(MyApp.db.userDao())
    val userViewModel = UserViewModelFactory(userRepository)
        .create(UserViewModel::class.java)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Home) { HomeScreen(navController) }
        composable(Routes.History) { HistoryScreen(navController) }
        composable(Routes.Add) { AddScreen(navController) }
        composable(Routes.Report) { ReportScreen(navController) }
        composable(Routes.Profile) { ProfileScreen(navController, userViewModel) }
        composable(Routes.Settings) { SettingsScreen(navController) }

        // Hồ sơ người dùng
        composable("${Routes.UserProfile}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: "guest"
            val userRepository = UserRepository(MyApp.db.userDao())
            val viewModel = UserViewModelFactory(userRepository).create(UserViewModel::class.java)
            UserProfileScreen(navController, userId, viewModel)
        }

        composable("${Routes.EditProfile}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: "guest"
            val userRepository = UserRepository(MyApp.db.userDao())
            val viewModel = UserViewModelFactory(userRepository).create(UserViewModel::class.java)
            EditProfileScreen(navController, userId, viewModel)
        }




        // Cài đặt
        composable(Routes.ThemeSettings) { ThemeSettingScreen(navController) }
        composable(Routes.LanguageSettings) { LanguageSettingScreen(navController) }
        composable(Routes.FontSizeSettings) { FontSizeScreen(navController) }

        // Thêm 2 màn hình mới
        composable(Routes.CategorySettings) { CategorySettingScreen(navController) }
        composable(Routes.AddExpenseCategory) { AddExpenseCategoryScreen(navController) }
        composable(Routes.AddIncomeCategory) { AddIncomeCategoryScreen(navController) }
        composable(Routes.CurrencySettings) { CurrencySettingScreen(navController) }

        composable(Routes.AddTransaction) { AddTransactionScreen(navController) }

        // ✅ Route xem chi tiết giao dịch
        composable("${Routes.TransactionDetail}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: -1
            TransactionDetailScreen(navController = navController, transactionId = id)
        }
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

    const val AddTransaction = "add_transaction"
    const val AddExpenseCategory = "add_expense_category"
    const val AddIncomeCategory = "add_income_category"
    const val CurrencySettings = "currency_settings"

    // ➕ Route mới
    const val TransactionDetail = "detail"
}
