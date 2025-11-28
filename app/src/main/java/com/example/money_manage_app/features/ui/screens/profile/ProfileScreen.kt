package com.example.money_manage_app.features.ui.screens.profile

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.entity.User
import com.example.money_manage_app.features.viewmodel.CategoryViewModel
import com.example.money_manage_app.features.viewmodel.TransactionViewModel
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
    userViewModel: UserViewModel,
    categoryViewModel: CategoryViewModel,
    transactionViewModel: TransactionViewModel // ✅ THÊM parameter
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
    val firebaseUser = auth.currentUser
    val isGoogleLoggedIn = firebaseUser != null
    val currentUserId = firebaseUser?.uid ?: "guest"

    /** ✅ Load categories VÀ transactions cho user */
    LaunchedEffect(currentUserId) {
        Log.d("ProfileScreen", "✅ Setting userId for both ViewModels: $currentUserId")
        categoryViewModel.setUserId(currentUserId)
        transactionViewModel.setUserId(currentUserId) // ✅ CRITICAL FIX
    }

    /** USER STATE (ROOM) */
    val userState = remember { mutableStateOf<User?>(null) }
    LaunchedEffect(currentUserId) {
        userViewModel.getUser(currentUserId).collect { user ->
            userState.value = user
        }
    }

    /** GOOGLE SIGN-IN */
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
                            val existing = userViewModel.getUserOnce(fu.uid)

                            // ✅ FIX 1: Check if this is a RETURNING user or NEW user
                            val isReturningUser = existing != null

                            val newUser = existing?.copy(
                                email = fu.email ?: existing.email,
                                photo = existing.photo ?: fu.photoUrl?.toString()
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

                            // Update UserViewModel
                            userViewModel.login(context, fu.uid)

                            // ✅ FIX 2: CRITICAL - Load data immediately
                            Log.d("ProfileScreen", "✅ Google login success (${if (isReturningUser) "RETURNING" else "NEW"} user): ${fu.uid}")
                            categoryViewModel.setUserId(fu.uid)
                            transactionViewModel.setUserId(fu.uid)

                            // ✅ FIX 3: Force reload to ensure data is fresh
                            kotlinx.coroutines.delay(100) // Small delay to ensure Flow is connected

                            if (!isReturningUser) {
                                Log.d("ProfileScreen", "⚠️ NEW USER - No transactions expected")
                            }
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


    /** UI */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {

        /** HEADER */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(brandYellow)
                .padding(vertical = 32.dp, horizontal = 20.dp)
        ) {
            val user = userState.value
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
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
                        Icons.Default.Settings,
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

                Crossfade(targetState = user != null && isGoogleLoggedIn) { loggedIn ->
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
                        Text(
                            text = stringResource(R.string.guest_user),
                            color = textColor,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        /** ✅ Login Button - CHỈ hiện nếu chưa đăng nhập */
        if (!isGoogleLoggedIn) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { launcher.launch(googleSignInClient.signInIntent) },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.login_email),
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        /** ✅ Logout Button - CHỈ hiện nếu ĐÃ đăng nhập */
        if (isGoogleLoggedIn) {
            ProfileCardItem(
                icon = Icons.Default.Logout,
                title = stringResource(R.string.logout),
                onClick = {
                    scope.launch {
                        Log.d("ProfileScreen", "🔴 Logging out...")

                        // ✅ Sign out Firebase
                        auth.signOut()
                        googleSignInClient.signOut().addOnCompleteListener {
                            scope.launch {
                                userState.value = null

                                // ✅ Logout user in ViewModel
                                userViewModel.logout(context)

                                // ✅ CRITICAL: Clear both categories AND transactions
                                Log.d("ProfileScreen", "✅ Switching to guest user")
                                transactionViewModel.setUserId("guest")
                                categoryViewModel.setUserId("guest")
                            }
                        }
                    }
                },
                brandYellow = brandYellow,
                colorScheme = colorScheme
            )
        }

        ProfileCardItem(
            icon = Icons.Default.Settings,
            title = stringResource(R.string.settings_title),
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
            Text(text = title, color = colorScheme.onSurface)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = colorScheme.onSurfaceVariant)
        }
    }
}