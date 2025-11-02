package com.example.money_manage_app.features.ui

import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
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
    // ✅ Tạo NavController riêng cho phần bottom navigation
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem("home", R.string.home, Icons.Default.Home),
        BottomNavItem("history", R.string.history, Icons.Default.History),
        BottomNavItem("report", R.string.report, Icons.Default.Article),
        BottomNavItem("profile", R.string.profile, Icons.Default.Person)
    )

    val colors = MaterialTheme.colorScheme
    val activeColor = Color(0xFFFEE912)
    val inactiveColor = colors.onSurface

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = colors.surface,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    val selected = currentRoute == item.route

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (currentRoute != item.route) {
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
    ) { innerPadding ->
        // ✅ NavGraph dùng navController riêng
        NavGraph(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .background(colors.background)
        )
    }
}
