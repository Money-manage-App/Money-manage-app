package com.example.money_manage_app.features.ui.screens.settings.userprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavHostController,
    userId: String,
    userViewModel: UserViewModel
) {
    val user by userViewModel.getUser(userId).collectAsState(initial = null)
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hồ sơ người dùng", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            Spacer(Modifier.height(32.dp))
            // Avatar
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape)
            ) {
                val photoUrl = user?.photo
                if (!photoUrl.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUrl),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(colors.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.name?.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                ProfileItem("Họ & tên", user?.name ?: "", "Chưa có tên")
                ProfileItem("Email", user?.email ?: "", "Chưa có email")
                ProfileItem("Số điện thoại", user?.phone ?: "", "Chưa có số điện thoại")
                ProfileItem(
                    "Giới tính",
                    when(user?.gender){
                        true -> "Nam"
                        false -> "Nữ"
                        else -> "Chưa chọn"
                    },
                    "Chưa chọn"
                )
            }

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate("edit_profile/$userId") },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
                Spacer(Modifier.width(8.dp))
                Text("Chỉnh sửa")
            }
        }
    }
}

@Composable
private fun ProfileItem(label: String, value: String, noValueText: String) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, color = colors.onBackground)
        Text(
            text = if (value.isNotEmpty()) value else noValueText,
            color = colors.onBackground.copy(alpha = 0.8f)
        )
        Divider(color = colors.outline.copy(alpha = 0.3f), thickness = 1.dp)
    }
}
