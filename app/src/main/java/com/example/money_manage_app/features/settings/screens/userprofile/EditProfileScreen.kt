package com.example.money_manage_app.features.settings.screens.userprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import com.example.money_manage_app.features.settings.data.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userInfo by userPrefs.userInfo.collectAsState(initial = mapOf())

    var name by remember { mutableStateOf(TextFieldValue(userInfo["name"] ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(userInfo["email"] ?: "")) }
    var phone by remember { mutableStateOf(TextFieldValue(userInfo["phone"] ?: "")) }

    var gender by remember { mutableStateOf(userInfo["gender"] ?: "") }
    var birthday by remember { mutableStateOf(userInfo["birthday"] ?: "") }

    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.edit_profile_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = colors.onPrimary,
                    navigationIconContentColor = colors.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(R.string.phone_number)) },
                modifier = Modifier.fillMaxWidth()
            )

            // ✅ Giới tính
            Text("Giới tính", style = MaterialTheme.typography.titleMedium)

            Row {
                RadioButton(
                    selected = gender == "Nam",
                    onClick = { gender = "Nam" }
                )
                Text("Nam")

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = gender == "Nữ",
                    onClick = { gender = "Nữ" }
                )
                Text("Nữ")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        // ✅ Lưu cả gender & birthday
                        userPrefs.saveUserInfo(
                            name.text,
                            email.text,
                            phone.text,
                            gender,
                            birthday
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text(stringResource(R.string.save_changes))
            }
        }
    }
}
