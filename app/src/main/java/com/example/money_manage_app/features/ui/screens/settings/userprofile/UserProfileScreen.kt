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
import com.example.money_manage_app.MyApp
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
                title = {
                    Text(
                        text = stringResource(R.string.user_profile_title),
                        color = Color.Black,
                        style = typography.titleMedium.copy(fontSize = 20.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.primary,
                    titleContentColor = colors.onPrimary
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
            Spacer(modifier = Modifier.height(32.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            ) {
                if (!user?.photo.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(user!!.photo!!),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.secondaryContainer),
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

            Spacer(modifier = Modifier.height(20.dp))

            // Thông tin user
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                ProfileItem(stringResource(R.string.full_name), user?.name ?: "", stringResource(R.string.no_name))
                ProfileItem("Email", user?.email ?: "", stringResource(R.string.no_email))
                ProfileItem(stringResource(R.string.phone_number), user?.phone ?: "", stringResource(R.string.no_phone))
                ProfileItem(stringResource(R.string.gender), user?.gender ?: "", stringResource(R.string.no_gender))
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate("edit_profile/$userId") },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.edit_profile_title))
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
