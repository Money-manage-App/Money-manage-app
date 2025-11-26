// ProfileScreen.kt
package com.example.money_manage_app.features.ui.screens.profile

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.entity.User
import com.example.money_manage_app.features.viewmodel.UserViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val isDark = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val brandYellow = if (isDark) Color(0xFFFBC02D) else Color(0xFFFEE912)
    val textColor = if (isDark) Color.White else Color.Black

    SideEffect {
        systemUiController.setStatusBarColor(brandYellow, darkIcons = !isDark)
    }

    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: "guest"

    // State user Room
    val userState = remember { mutableStateOf<User?>(null) }

    // Observe Room user
    LaunchedEffect(currentUserId) {
        userViewModel.getUser(currentUserId).collect { user ->
            userState.value = user
        }
    }

    // Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("670135857127-7l1sc670mf6vr4edtfo0kud4uk5dctj8.apps.googleusercontent.com")
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    val activity = context as Activity
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener(activity) { signInTask ->
                if (signInTask.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { fu ->
                        scope.launch {
                            val existingUser = userViewModel.getUserOnce(fu.uid)
                            val newUser = existingUser?.copy(
                                email = fu.email ?: existingUser.email,
                                photo = existingUser.photo ?: fu.photoUrl?.toString()
                            ) ?: User(
                                userId = fu.uid,
                                name = fu.displayName ?: "",
                                email = fu.email,
                                phone = null,
                                gender = null,
                                photo = fu.photoUrl?.toString(),
                                isGuest = false,
                                createdAt = System.currentTimeMillis()
                            )
                            userViewModel.saveUser(newUser)
                            userState.value = newUser
                        }
                    }
                } else {
                    Log.e("ProfileScreen", "Firebase auth failed", signInTask.exception)
                }
            }
        } catch (e: ApiException) {
            Log.e("ProfileScreen", "Google sign in failed", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // Header vàng
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(brandYellow)
                .padding(vertical = 32.dp, horizontal = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                val user = userState.value
                if (user?.photo != null) {
                    Image(
                        painter = rememberAsyncImagePainter(user.photo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(16.dp),
                        tint = textColor
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Crossfade(targetState = user != null) { loggedIn ->
                    if (loggedIn && user != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = user.name.ifBlank { "Người dùng" },
                                color = textColor,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = user.email ?: "",
                                color = textColor.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Button(
                            onClick = { launcher.launch(googleSignInClient.signInIntent) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Đăng nhập bằng Google",
                                color = Color.Black,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        // Options
        if (userState.value != null) {
            ProfileCardItem(
                icon = Icons.Default.Logout,
                title = "Đăng xuất",
                onClick = {
                    auth.signOut()
                    googleSignInClient.signOut()
                    userState.value = null
                },
                brandYellow = brandYellow,
                colorScheme = colorScheme
            )
        }
        ProfileCardItem(
            icon = Icons.Default.Settings,
            title = "Cài đặt",
            onClick = { navController.navigate("settings") },
            brandYellow = brandYellow,
            colorScheme = colorScheme
        )
    }
}

@Composable
fun ProfileCardItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    brandYellow: Color,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = brandYellow)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = colorScheme.onSurfaceVariant)
        }
    }
}
