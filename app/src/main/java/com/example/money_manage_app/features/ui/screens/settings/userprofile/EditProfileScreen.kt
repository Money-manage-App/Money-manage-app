package com.example.money_manage_app.features.ui.screens.settings.userprofile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.money_manage_app.R
import com.example.money_manage_app.features.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// EditProfileScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    userId: String,
    userViewModel: UserViewModel
) {
    val user by userViewModel.getUser(userId).collectAsState(initial = null)
    val colors = MaterialTheme.colorScheme
    val auth = FirebaseAuth.getInstance()
    val isGoogleAccount = auth.currentUser?.providerData?.any { it.providerId == "google.com" } == true

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf<Boolean?>(null) }
    var photo by remember { mutableStateOf("") }

    // Regex kiểm tra số điện thoại hợp lệ
    val phoneRegex = Regex("^0\\d{9}\$")

    // Snackbar để hiển thị lỗi khi lưu
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            email = it.email ?: ""
            phone = it.phone ?: ""
            gender = it.gender
            photo = it.photo ?: ""
        }
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { photo = it.toString() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.edit_profile_title), color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    val scope = rememberCoroutineScope()

                    IconButton(onClick = {
                        if (!phoneRegex.matches(phone)) {
                            // Sử dụng CoroutineScope để hiển thị Snackbar
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số"
                                )
                            }
                            return@IconButton
                        }

                        user?.let {
                            val updated = it.copy(
                                name = name,
                                email = if (isGoogleAccount) it.email else email.ifEmpty { null },
                                phone = phone,
                                gender = gender,
                                photo = photo
                            )
                            userViewModel.updateUser(updated)
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.Black)
                    }

                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFEE912)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (photo.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(photo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth(0.85f)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { if (!isGoogleAccount) email = it },
                label = { Text(stringResource(R.string.email)) },
                enabled = !isGoogleAccount,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it }, // Cho phép nhập tự do
                label = { Text(stringResource(R.string.phone_number)) },
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = gender == true, onClick = { gender = true }, modifier = Modifier.size(28.dp))
                    Text(stringResource(R.string.male), fontSize = 18.sp, modifier = Modifier.padding(start = 4.dp))
                }
                Spacer(modifier = Modifier.width(70.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = gender == false, onClick = { gender = false }, modifier = Modifier.size(28.dp))
                    Text(stringResource(R.string.female), fontSize = 18.sp, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}
