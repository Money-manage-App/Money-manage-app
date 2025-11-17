package com.example.money_manage_app.features.ui.screens.settings.userprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userInfo by userPrefs.userInfo.collectAsState(initial = mapOf())
    val scope = rememberCoroutineScope()

    val name = userInfo["name"] ?: ""
    val email = userInfo["email"] ?: ""
    val phone = userInfo["phone"] ?: ""
    val gender = userInfo["gender"] ?: ""
    val photo = userInfo["photo"] ?: ""

    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.user_profile_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
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
                .background(colors.background)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            ) {
                if (photo.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(photo),
                        contentDescription = stringResource(R.string.profile),
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
                            text = if (name.isNotEmpty()) name.first().uppercase() else "?",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                ProfileItem(
                    label = stringResource(R.string.full_name),
                    value = name,
                    noValueText = stringResource(R.string.no_name)
                )
                ProfileItem(
                    label = stringResource(R.string.email),
                    value = email,
                    noValueText = stringResource(R.string.no_email)
                )
                ProfileItem(
                    label = stringResource(R.string.phone_number),
                    value = phone,
                    noValueText = stringResource(R.string.no_phone)
                )
                ProfileItem(
                    label = stringResource(R.string.gender),
                    value = gender,
                    noValueText = stringResource(R.string.no_gender)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate("edit_profile") },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.edit_profile))
            }
        }
    }
}

@Composable
private fun ProfileItem(label: String, value: String, noValueText: String) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground
        )
        Text(
            text = if (value.isNotEmpty()) value else noValueText,
            color = colors.onBackground.copy(alpha = 0.8f)
        )
        Divider(
            color = colors.outline.copy(alpha = 0.3f),
            thickness = 1.dp
        )
    }
}