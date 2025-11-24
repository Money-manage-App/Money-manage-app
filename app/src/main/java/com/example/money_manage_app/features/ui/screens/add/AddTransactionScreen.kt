package com.example.money_manage_app.features.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.money_manage_app.R

data class Category(
    val name: String,
    val icon: ImageVector,
    val isExpense: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var showInputDialog by remember { mutableStateOf(false) }

    val expenseCategories = listOf(
        Category("Ăn uống", Icons.Default.ShoppingCart),
        Category("Đi lại", Icons.Default.DirectionsCar),
        Category("Quần áo", Icons.Default.CheckCircle),
        Category("Tạp hóa", Icons.Default.Home),
        Category("Hóa đơn", Icons.Default.Receipt),
        Category("Thể thao", Icons.Default.FitnessCenter),
        Category("Sức khỏe", Icons.Default.Favorite),
        Category("Sửa chữa", Icons.Default.Build),
        Category("Bảo hiểm", Icons.Default.Security)
    )

    val incomeCategories = listOf(
        Category("Lương", Icons.Default.AccountBalance, false),
        Category("Khoản tiết kiệm", Icons.Default.Savings, false),
        Category("Tiền lãi", Icons.Default.TrendingUp, false),
        Category("Khác", Icons.Default.MoreHoriz, false)
    )

    val categories = if (selectedTab == 0) expenseCategories else incomeCategories

    if (showInputDialog && selectedCategory != null) {
        TransactionInputDialog(
            category = selectedCategory!!,
            onDismiss = {
                showInputDialog = false
                selectedCategory = null
            },
            onConfirm = { amount, note ->
                showInputDialog = false
                selectedCategory = null
                navController.navigate("history") {
                    // Xóa toàn bộ stack để không thể back lại màn hình Add
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD600))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(120.dp))
                Text(
                    text = stringResource(R.string.add),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color(0xFFE8E8E8), RoundedCornerShape(6.dp))
            ) {}

            Row(modifier = Modifier.fillMaxWidth()) {
                TabButton(
                    text = stringResource(R.string.expense),
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = stringResource(R.string.income),
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            categories.chunked(4).forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowCategories.forEach { category ->
                        CategoryItem(
                            category = category,
                            isSelected = false,
                            onClick = {
                                selectedCategory = category
                                showInputDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(4 - rowCategories.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionInputDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var amount by remember { mutableStateOf("0") }
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var selectedCalendar by remember {
        mutableStateOf(java.util.Calendar.getInstance())
    }

    val currentDateTime by remember {
        derivedStateOf {
            val day = selectedCalendar.get(java.util.Calendar.DAY_OF_MONTH)
            val month = selectedCalendar.get(java.util.Calendar.MONTH) + 1
            val year = selectedCalendar.get(java.util.Calendar.YEAR)
            val hour = selectedCalendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = selectedCalendar.get(java.util.Calendar.MINUTE)
            "${String.format("%02d", day)}/${String.format("%02d", month)}/$year ${String.format("%02d:%02d", hour, minute)}"
        }
    }

    // Thay thế phần DatePickerDialog trong TransactionInputDialog

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedCalendar.timeInMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newCalendar = java.util.Calendar.getInstance()
                        newCalendar.timeInMillis = millis
                        newCalendar.set(java.util.Calendar.HOUR_OF_DAY, selectedCalendar.get(java.util.Calendar.HOUR_OF_DAY))
                        newCalendar.set(java.util.Calendar.MINUTE, selectedCalendar.get(java.util.Calendar.MINUTE))
                        selectedCalendar = newCalendar
                    }
                    showDatePicker = false
                    showTimePicker = true
                }) {
                    Text("OK", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel), color = Color.Black)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White  // Nền dialog màu trắng
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,  // Nền lịch màu trắng
                    titleContentColor = Color.Black,  // Màu chữ "Chọn ngày" màu đen
                    headlineContentColor = Color.Black,  // Màu chữ tiêu đề ngày được chọn
                    selectedDayContainerColor = Color(0xFFFEE912),  // Ngày được chọn màu vàng FEE912
                    selectedDayContentColor = Color.Black,  // Màu chữ của ngày được chọn
                    todayContentColor = Color.Black,  // Màu chữ ngày hôm nay
                    todayDateBorderColor = Color.Transparent,  // Không có viền ngày hôm nay
                    dayContentColor = Color.Black,  // Màu chữ các ngày thường
                    weekdayContentColor = Color.Black,  // Màu chữ thứ trong tuần
                    yearContentColor = Color.Black,  // Màu chữ năm
                    currentYearContentColor = Color.Black,  // Màu chữ năm hiện tại
                    selectedYearContainerColor = Color(0xFFFEE912),  // Nền năm được chọn
                    selectedYearContentColor = Color.Black,  // Màu chữ năm được chọn
                    navigationContentColor = Color.Black,  // Màu mũi tên điều hướng
                    dividerColor = Color.Transparent  // Ẩn đường phân cách
                )
            )
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedCalendar.get(java.util.Calendar.HOUR_OF_DAY),
            initialMinute = selectedCalendar.get(java.util.Calendar.MINUTE),
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text (stringResource(R.string.select_time))},
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFFFFF9C4),
                        selectorColor = Color(0xFFFFD600),
                        timeSelectorSelectedContainerColor = Color(0xFFFFD600)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val newCalendar = selectedCalendar.clone() as java.util.Calendar
                    newCalendar.set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                    newCalendar.set(java.util.Calendar.MINUTE, timePickerState.minute)
                    selectedCalendar = newCalendar
                    showTimePicker = false
                }) {
                    Text("OK", color = Color(0xFFFFD600))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel), color = Color.Gray)
                }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFFD600), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = category.name,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = category.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .clickable { showDatePicker = true }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentDateTime,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Pick Date",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Số tiền") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD600),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFFD600)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ghi chú") },
                    placeholder = { Text("Nhập ghi chú...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD600),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFFD600)
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel), color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(amount, note) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD600)
                        )
                    ) {
                        Text(stringResource(R.string.confirm), color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .padding(4.dp)
            .then(
                if (isSelected) {
                    Modifier.background(Color.White, RoundedCornerShape(4.dp))
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isSelected) Color(0xFFFFD600) else Color.White,
                    shape = CircleShape
                )
                .border(
                    width = if (isSelected) 0.dp else 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.name,
            fontSize = 11.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}