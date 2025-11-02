package com.example.money_manage_app.features.profile.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.money_manage_app.R
import com.example.money_manage_app.features.settings.data.LanguagePreference
import com.example.money_manage_app.features.settings.data.UserPreferences
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {

    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    val languagePref = remember { LanguagePreference(context) }
    val currentLanguage by languagePref.currentLanguage.collectAsState(initial = "Tiếng Việt")

    val userPrefs = remember { UserPreferences(context) }
    val userInfo by userPrefs.userInfo.collectAsState(initial = mapOf())

    val auth = FirebaseAuth.getInstance()

    // ✅ Reactive Firebase user
    var firebaseUser by remember { mutableStateOf(auth.currentUser) }

    LaunchedEffect(Unit) {
        auth.currentUser?.reload()?.addOnSuccessListener {
            firebaseUser = auth.currentUser
        }
    }

    // ✅ Observe auth changes (fix UI not updating after login)
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener {
            firebaseUser = it.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    val brandYellow = if (isDark) Color(0xFFFBC02D) else Color(0xFFFEE912)
    val textColor = if (isDark) Color.White else Color.Black

    SideEffect {
        systemUiController.setStatusBarColor(brandYellow, darkIcons = !isDark)
    }

    // ✅ Google Sign-In setup
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("670135857127-7l1sc670mf6vr4edtfo0kud4uk5dctj8.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    val activity = context as? Activity

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity!!) { signInTask ->
                    if (signInTask.isSuccessful) {
                        val user = auth.currentUser

                        user?.reload()?.addOnSuccessListener {
                            firebaseUser = auth.currentUser
                        }

                        scope.launch {
                            userPrefs.saveUserInfo(
                                name = user?.displayName ?: "",
                                email = user?.email ?: "",
                                phone = user?.phoneNumber ?: "",
                                gender = "",
                                birthday = ""
                            )
                        }
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ✅ HEADER UI
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(brandYellow)
                .padding(vertical = 40.dp, horizontal = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Crossfade(targetState = firebaseUser != null) { loggedIn ->
                    if (loggedIn) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            if (firebaseUser?.photoUrl != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(firebaseUser?.photoUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(12.dp),
                                    tint = textColor
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = firebaseUser?.displayName ?: "Người dùng",
                                    color = textColor,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = firebaseUser?.email ?: "",
                                    color = textColor.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = if (currentLanguage == "English") "Welcome!" else "Chào mừng!",
                                color = textColor,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = if (currentLanguage == "English") "Sign in to continue" else "Đăng nhập để tiếp tục",
                                color = textColor.copy(alpha = 0.9f)
                            )

                            Spacer(Modifier.height(16.dp))

                            Card(
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, Color.LightGray),
                                modifier = Modifier
                                    .shadow(6.dp, RoundedCornerShape(50))
                                    .clickable { launcher.launch(googleSignInClient.signInIntent) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_google_logo),
                                        contentDescription = "Google",
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = if (currentLanguage == "English") "Continue with Google" else "Tiếp tục với Google",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ✅ Logout
        if (firebaseUser != null) {
            ProfileCardItem(
                icon = Icons.Default.Logout,
                title = if (currentLanguage == "English") "Sign out" else "Đăng xuất",
                onClick = {
                    // Xoá Firebase session
                    auth.signOut()

                    // Update UI ngay lập tức
                    firebaseUser = null

                    // Xoá dữ liệu người dùng đã lưu trong SharedPreferences
                    scope.launch {
                        userPrefs.clearUserInfo()
                    }

                    // Revoke + SignOut Google -> buộc chọn lại tài khoản khi login
                    googleSignInClient.revokeAccess().addOnCompleteListener {
                        googleSignInClient.signOut().addOnCompleteListener {
                            // Done
                        }
                    }
                },
                        brandYellow = brandYellow,
                colorScheme = MaterialTheme.colorScheme
            )
        }

        // ✅ Settings
        ProfileCardItem(
            icon = Icons.Default.Settings,
            title = if (currentLanguage == "English") "Settings" else "Cài đặt",
            onClick = { navController.navigate("settings") },
            brandYellow = brandYellow,
            colorScheme = MaterialTheme.colorScheme
        )
    }
}

@Composable
fun ProfileCardItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            Spacer(Modifier.width(12.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface))
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = colorScheme.onSurfaceVariant)
        }
    }
}
