package com.example.money_manage_app.features.ui.screens.report

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportScreen(navController: NavHostController) {

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }

    var selectingStart by remember { mutableStateOf(true) }

    // --- Dialog chọn ngày ---
    fun showDatePicker(onDateSelected: (Date) -> Unit) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        // Ô chọn khoảng ngày
        Surface(
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable {
                    if (startDate == null) {
                        // Chọn ngày bắt đầu
                        selectingStart = true
                        showDatePicker { date ->
                            startDate = date
                        }
                    } else {
                        // Chọn ngày kết thúc
                        selectingStart = false
                        showDatePicker { date ->
                            endDate = date
                        }
                    }
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = when {
                        startDate == null -> "Chọn khoảng thời gian"
                        endDate == null -> "${dateFormatter.format(startDate!!)} - ..."
                        else -> "${dateFormatter.format(startDate!!)} - ${dateFormatter.format(endDate!!)}"
                    },
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Card tổng và chi tiết
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tổng số dư", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("xxx.xxx.xxx đ", color = Color(0xFF2ECC71), fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Chi tiêu
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFFFECEB), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Chi tiêu", fontWeight = FontWeight.Bold)
                        Text("x.xxx.xxx đ", color = Color(0xFFE74C3C), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Trung bình/ngày: xxx.xxx", fontSize = 12.sp, color = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Thu nhập
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFE9F8EF), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Thu nhập", fontWeight = FontWeight.Bold)
                        Text("x.xxx.xxx đ", color = Color(0xFF27AE60), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Trung bình/ngày: xxx.xxx", fontSize = 12.sp, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}
