package com.example.money_manage_app.features.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import com.example.money_manage_app.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header vàng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFD600))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(100.dp))
                    Text(
                        text = stringResource(R.string.note_transaction),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }

            // Content area với text ở giữa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Xin chào, ...!",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Bottom bar với 3 nút (fixed ở dưới cùng)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Đường kẻ mờ phía trên
            Column {
                Divider(
                    thickness = 0.5.dp,
                    color = Color(0xFFE0E0E0)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA))
                        .padding(vertical =35.dp, horizontal = 60.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút keyboard
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFBDBDBD), CircleShape)
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

                    // Nút microphone (chính giữa - lớn hơn)
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Black, CircleShape)
                            .clickable { /* Xử lý voice */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = Color(0xFFFFD600),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Nút note - Click vào đây sẽ mở AddScreen
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFBDBDBD), CircleShape)
                            .clickable {
                                // Navigate tới AddScreen
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