package com.example.money_manage_app.features.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.money_manage_app.R
import com.example.money_manage_app.features.navigation.NavGraph
import com.example.money_manage_app.features.navigation.Routes

data class BottomNavItem(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector
)

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem(Routes.Home, R.string.home, Icons.Default.Home),
        BottomNavItem(Routes.History, R.string.history, Icons.Default.History),
        BottomNavItem(Routes.Report, R.string.report, Icons.Default.Article),
        BottomNavItem(Routes.Profile, R.string.profile, Icons.Default.Person)
    )

    val colors = MaterialTheme.colorScheme
    val activeColor = Color(0xFFFEE912)
    val inactiveColor = colors.onSurface

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),

        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // ✅ Những màn hình sẽ ẩn BottomNav
            val hideBottomBarRoutes = listOf(
                Routes.Add,
                Routes.Settings,
                Routes.UserProfile,
                Routes.EditProfile,
                Routes.ThemeSettings,
                Routes.LanguageSettings,
                Routes.FontSizeSettings,
                Routes.AddTransaction
            )

            val shouldShowBottomBar = currentRoute !in hideBottomBarRoutes

            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = colors.surface,
                    tonalElevation = 0.dp
                ) {

                    val navEntry by navController.currentBackStackEntryAsState()
                    val route = navEntry?.destination?.route

                    // --- LEFT ITEMS: Home + History ---
                    items.take(2).forEach { item ->
                        val selected = route == item.route

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (route != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.titleRes),
                                    tint = if (selected) activeColor else inactiveColor
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(item.titleRes),
                                    color = if (selected) activeColor else inactiveColor
                                )
                            }
                        )
                    }

                    // --- CENTER ADD BUTTON ---
                    FloatingActionButton(
                        onClick = { navController.navigate(Routes.Add) },
                        containerColor = Color(0xFFFEE912),
                        contentColor = Color.Black,
                        shape = androidx.compose.foundation.shape.CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.Black
                        )
                    }

                    // --- RIGHT ITEMS: Report + Profile ---
                    items.takeLast(2).forEach { item ->
                        val selected = route == item.route

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (route != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.titleRes),
                                    tint = if (selected) activeColor else inactiveColor
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(item.titleRes),
                                    color = if (selected) activeColor else inactiveColor
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .background(colors.background)
        )
    }
}