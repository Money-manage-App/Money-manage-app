package com.example.money_manage_app.features.report.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.TextButton
import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavHostController) {
    val colors = MaterialTheme.colorScheme

    // Bộ định dạng ngày tháng
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy") }

    // Trạng thái Date Range Picker
    val dateRangePickerState = rememberDateRangePickerState()

    // Hiển thị dialog hay không
    var showDialog by remember { mutableStateOf(false) }

    // Lưu giá trị hiển thị ra giao diện
    val startDate = dateRangePickerState.selectedStartDateMillis?.let { dateFormatter.format(Date(it)) }
    val endDate = dateRangePickerState.selectedEndDateMillis?.let { dateFormatter.format(Date(it)) }

    // --- GIAO DIỆN CHÍNH ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // ✅ CHỌN NGÀY (bấm mở Date Range Picker)
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFEE912)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true } // 👈 Thêm sự kiện mở DatePicker
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (startDate != null && endDate != null)
                        "$startDate - $endDate"
                    else
                        stringResource(R.string.report_date_range),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ TỔNG SỐ DƯ
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.total_balance),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "xxx.xxx.xxx đ",
                    color = Color(0xFF2ECC71),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // ✅ Ô CHI TIÊU
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFFFECEB), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.expense),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "x.xxx.xxx đ",
                            color = Color(0xFFE74C3C),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.average_per_day, "xxx.xxx"),
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // ✅ Ô THU NHẬP
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFE9F8EF), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.income),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "x.xxx.xxx đ",
                            color = Color(0xFF27AE60),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.average_per_day, "xxx.xxx"),
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }

    // --- DIALOG CHỌN NGÀY ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Hủy") }
            },
            text = {
                DateRangePicker(state = dateRangePickerState)
            }
        )
    }
}
