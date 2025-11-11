package com.example.money_manage_app.features.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R

@Composable
fun AddScreen(navController: NavHostController) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ✅ Thanh tiêu đề vàng
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEE912))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Text(
                text = "Ghi chú giao dịch",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // ✅ Nội dung giữa màn hình
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Xin chào, ...!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        }

        // ✅ Thanh công cụ dưới
        BottomActionBar()
    }
}

@Composable
fun BottomActionBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(24.dp))
                .padding(vertical = 16.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ✅ Nút bàn phím
            IconButton(
                onClick = {},
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_keyboard),
                    contentDescription = "Keyboard",
                    tint = Color.Gray,
                    modifier = Modifier.size(45.dp)
                )
            }

            // ✅ Nút micro (đen - vàng)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Black, shape = CircleShape)
                    .clickable { }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = Color(0xFFFEE912),
                    modifier = Modifier.size(32.dp)
                )
            }

            // ✅ Nút ghi chú
            IconButton(
                onClick = {},
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_note),
                    contentDescription = "Note",
                    tint = Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}