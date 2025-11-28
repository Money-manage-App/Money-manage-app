package com.example.money_manage_app.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.money_manage_app.MyApp
import com.example.money_manage_app.data.repository.CategoryRepository
import com.example.money_manage_app.data.repository.UserRepository
import com.example.money_manage_app.data.repository.TransactionRepository

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

import com.example.money_manage_app.features.ui.screens.history.TransactionDetailScreen
import com.example.money_manage_app.features.viewmodel.CategoryViewModel
import com.example.money_manage_app.features.viewmodel.UserViewModel
import com.example.money_manage_app.features.viewmodel.UserViewModelFactory
import com.example.money_manage_app.features.viewmodel.TransactionViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startAtSettings: Boolean = false
) {
    val startDestination = if (startAtSettings) Routes.Settings else Routes.Home
    val context = LocalContext.current

    // ✅ Tạo UserRepository và UserViewModel 1 LẦN duy nhất
    val userRepository = remember { UserRepository(MyApp.db.userDao()) }
    val userViewModel = remember {
        UserViewModelFactory(userRepository).create(UserViewModel::class.java)
    }

    // ✅ Tạo CategoryRepository và CategoryViewModel 1 LẦN duy nhất
    val categoryRepository = remember { CategoryRepository(MyApp.db.categoryDao()) }
    val categoryViewModel = remember { CategoryViewModel(categoryRepository) }

    // ✅ Tạo TransactionRepository và TransactionViewModel 1 LẦN duy nhất
    val transactionRepository = remember { TransactionRepository(MyApp.db.transactionDao()) }
    val transactionViewModel = remember { TransactionViewModel(transactionRepository) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Home) { HomeScreen(navController, transactionViewModel, userViewModel) }

        composable(Routes.History) {
            HistoryScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                userViewModel = userViewModel
            )
        }

        composable(Routes.Add) { AddScreen(navController) }
        composable(Routes.Report) { ReportScreen(navController, transactionViewModel, userViewModel) }

        composable(Routes.Profile) {
            ProfileScreen(navController, userViewModel, categoryViewModel,transactionViewModel = transactionViewModel)
        }

        composable(Routes.Settings) { SettingsScreen(navController) }

        // Hồ sơ người dùng
        composable("${Routes.UserProfile}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: "guest"
            UserProfileScreen(navController, userId, userViewModel)
        }

        composable("${Routes.EditProfile}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: "guest"
            EditProfileScreen(navController, userId, userViewModel)
        }

        // Cài đặt
        composable(Routes.ThemeSettings) { ThemeSettingScreen(navController) }
        composable(Routes.LanguageSettings) { LanguageSettingScreen(navController) }
        composable(Routes.FontSizeSettings) { FontSizeScreen(navController) }

        // ✅ Category Settings
        composable(Routes.CategorySettings) {
            CategorySettingScreen(
                navController = navController,
                categoryViewModel = categoryViewModel,
                userViewModel = userViewModel
            )
        }

        composable(Routes.AddExpenseCategory) {
            AddExpenseCategoryScreen(navController, categoryViewModel)
        }

        composable(Routes.AddIncomeCategory) {
            AddIncomeCategoryScreen(navController, categoryViewModel)
        }

        composable(Routes.CurrencySettings) { CurrencySettingScreen(navController) }

        // ✅ Add Transaction
        composable(Routes.AddTransaction) {
            AddTransactionScreen(
                navController = navController,
                categoryViewModel = categoryViewModel,
                userViewModel = userViewModel,
                transactionViewModel = transactionViewModel
            )
        }

        // ✅ Transaction Detail
        composable("${Routes.TransactionDetail}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: -1
            TransactionDetailScreen(
                navController = navController,
                transactionId = id,
                transactionViewModel = transactionViewModel
            )
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

    const val TransactionDetail = "detail"
}