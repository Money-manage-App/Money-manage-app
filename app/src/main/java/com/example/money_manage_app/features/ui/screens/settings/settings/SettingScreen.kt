package com.example.money_manage_app.features.ui.screens.settings.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.MyApp
import com.example.money_manage_app.R
import com.example.money_manage_app.features.navigation.Routes
import com.example.money_manage_app.features.viewmodel.UserViewModel
import com.example.money_manage_app.features.viewmodel.UserViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: "guest"

    // Tạo UserViewModel
    val userRepository = com.example.money_manage_app.data.repository.UserRepository(MyApp.db.userDao())
    val userViewModel = UserViewModelFactory(userRepository).create(UserViewModel::class.java)

    // Google sign-in client
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("670135857127-7l1sc670mf6vr4edtfo0kud4uk5dctj8.apps.googleusercontent.com")
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        color = Color.Black,
                        style = typography.titleMedium.copy(fontSize = 20.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFEE912)
                )
            )
        },
        containerColor = colors.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingItem(
                icon = Icons.Default.AccountCircle,
                title = stringResource(R.string.profile),
                onClick = {
                    // Chỉ navigate sang UserProfileScreen với userId
                    navController.navigate("${Routes.UserProfile}/$currentUserId")
                }
            )

            SettingItem(
                icon = Icons.Default.Category,
                title = stringResource(R.string.category_settings),
                onClick = { navController.navigate(Routes.CategorySettings) }
            )

            SettingItem(
                icon = Icons.Default.AttachMoney,
                title = stringResource(R.string.currency),
                onClick = { navController.navigate(Routes.CurrencySettings) }
            )

            SettingItem(
                icon = Icons.Default.DisplaySettings,
                title = stringResource(R.string.theme),
                onClick = { navController.navigate(Routes.ThemeSettings) }
            )

            SettingItem(
                icon = Icons.Default.TextFields,
                title = stringResource(R.string.font_size),
                onClick = { navController.navigate(Routes.FontSizeSettings) }
            )

            SettingItem(
                icon = Icons.Default.Language,
                title = stringResource(R.string.language),
                onClick = { navController.navigate(Routes.LanguageSettings) }
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        shadowElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFFFEE912)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "arrow",
                tint = Color(0xFFFEE912)
            )
        }
    }
}
