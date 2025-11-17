package com.example.money_manage_app.features.ui.screens.settings.settings

import com.example.money_manage_app.data.local.datastore.ThemePreference
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingScreen(navController: NavHostController) {

    val context = LocalContext.current
    val pref = remember { ThemePreference(context) }
    val scope = rememberCoroutineScope()
    val isDark by pref.isDarkMode.collectAsState(initial = false)

    val colors = MaterialTheme.colorScheme

    val selectedBorderColor = if (isDark) Color(0xFF90CAF9) else Color(0xFFFFC107)
    val selectedCheckColor = selectedBorderColor

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.theme),
                        color = colors.onPrimary,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
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

            listOf(
                stringResource(R.string.light_mode) to false,
                stringResource(R.string.dark_mode) to true
            ).forEach { (title, dark) ->

                val icon = if (!dark) Icons.Default.WbSunny else Icons.Default.Brightness2
                val isSelected = isDark == dark

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 6.dp)
                        .then(
                            if (isSelected)
                                Modifier.border(
                                    2.dp,
                                    selectedBorderColor,
                                    RoundedCornerShape(16.dp)
                                )
                            else Modifier
                        )
                        .clickable { scope.launch { pref.setDarkMode(dark) } },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val globalTint = if (isDark) Color.White else Color(0xFFFFC107)

                        Icon(icon, null, tint = globalTint)
                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            title,
                            fontSize = 16.sp,
                            color = colors.onSurface
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        if (isSelected) {
                            Icon(Icons.Default.Check, null, tint = globalTint)
                        }
                    }
                }
            }
        }
    }
}
