package com.example.money_manage_app.features.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import com.example.money_manage_app.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun AddScreen(navController: NavHostController) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background) // Thay Color(0xFFF8F8F8)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header vàng -> dùng primary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary) // Thay Color(0xFFFEE912)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.onPrimary, // Thay Color.Black
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(100.dp))
                    Text(
                        text = stringResource(R.string.note_transaction),
                        color = colors.onPrimary, // Thay Color.Black
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                    )
                }
            }

            // Content area với text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colors.background), // Thay Color.White
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.hello),
                    fontSize = 16.sp,
                    color = colors.onSurface, // Thay Color.Black
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Bottom bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column {
                Divider(
                    thickness = 0.5.dp,
                    color = colors.outline.copy(alpha = 0.5f) // Thay Color(0xFFE0E0E0)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface) // Thay Color(0xFFFAFAFA)
                        .padding(vertical = 35.dp, horizontal = 60.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút keyboard
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.Gray, CircleShape) // Có thể dùng colors.secondaryContainer
                            .clickable { /* Xử lý keyboard */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Keyboard",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Nút microphone
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(colors.primary, CircleShape) // theme-aware
                            .clickable { /* Xử lý voice */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = colors.onPrimary, // theme-aware
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Nút note
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.Gray, CircleShape)
                            .clickable {
                                navController.navigate("add_transaction")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Note",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}
