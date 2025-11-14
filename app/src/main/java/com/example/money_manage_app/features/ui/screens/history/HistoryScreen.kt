package com.example.money_manage_app.features.ui.screens.history

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.money_manage_app.R
import androidx.compose.material.icons.filled.*
import java.text.SimpleDateFormat
import java.util.*

data class Transaction(
    val id: Int,
    val title: String,
    val category: String,
    val amount: Double,
    val isIncome: Boolean,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())

    // Danh sách trống để hiển thị trạng thái "Không tìm thấy giao dịch nào"
    val transactions = remember { mutableStateListOf<Transaction>() }

    // State cho DatePicker
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header vàng với bo góc
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFE500),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 40.dp)
            ) {
                Text(
                    text = "Lịch sử",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            // Ô chọn ngày nằm một phần trên nền vàng
            Surface(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .offset(y = (-24).dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = dateFormatter.format(Date(selectedDate)),
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Chọn ngày",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(-12.dp))

            // Tiêu đề danh sách
            Text(
                text = "Danh Sách Giao Dịch",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp)
            )

            // Khi không có giao dịch
            if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_empty_state),
                        contentDescription = "Empty",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Không tìm thấy giao dịch nào",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("add") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A4A4A)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "+ ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD600)
                        )
                        Text(
                            text = "Thêm giao dịch",
                            fontSize = 15.sp,
                            color = Color.Yellow
                        )
                    }
                }
            } else {
                // Danh sách giao dịch khi có data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction = transaction)
                    }
                }
            }
        }

        // Calendar Popup Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                selectedDate = it
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK", color = Color(0xFF3C2E7E))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Hủy", color = Color.Gray)
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White
                )
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = Color(0xFF3C2E7E),
                        todayContentColor = Color(0xFF3C2E7E),
                        todayDateBorderColor = Color(0xFF3C2E7E)
                    )
                )
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = transaction.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = transaction.category, color = Color.Gray, fontSize = 14.sp)
        }

        Text(
            text = (if (transaction.isIncome) "+ " else "- ") + String.format("%,.0f đ", transaction.amount),
            color = if (transaction.isIncome) Color(0xFF00C853) else Color(0xFFD50000),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Divider(thickness = 1.dp, color = Color.LightGray)
}


