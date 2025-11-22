package com.example.money_manage_app.features.ui.screens.history

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.FontSizeManager
import com.example.money_manage_app.data.local.datastore.ThemePreference
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.draw.clip



data class Transaction(
    val id: Int,
    val title: String,
    val category: String,
    val amount: Double,
    val isIncome: Boolean,
    val time: String,
    val icon: ImageVector,
    val categoryColor: Color
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val fontSizeManager = remember { FontSizeManager(context) }
    val fontScale by fontSizeManager.fontSizeFlow.collectAsState(initial = 1f)

    val themePreference = remember { ThemePreference(context) }
    val isDarkMode by themePreference.isDarkMode.collectAsState(initial = false)

    val languagePreference = remember { LanguagePreference(context) }
    val currentLanguage by languagePreference.currentLanguage.collectAsState(initial = "Tiếng Việt")

    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val headerColor = Color(0xFFFEE912)
    val headerTextColor = Color.Black
    val textColor = if (isDarkMode) Color.White else Color.Black
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF3E3E3E) else Color.LightGray
    val buttonColor = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFF4A4A4A)
    val buttonTextColor = Color(0xFFFEE912)

    val iconColor = if (isDarkMode) Color.White else Color.Black
    val datecolor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())

    val transactions = remember {
        mutableStateListOf(
            Transaction(
                id = 1,
                title = "Bún bò",
                category = "Ăn uống",
                amount = 30000.0,
                isIncome = false,
                time = "15:30",
                icon = Icons.Default.CalendarToday,
                categoryColor = Color(0xFF4CAF50)
            ),
            Transaction(
                id = 2,
                title = "Quần áo",
                category = "Mua sắm",
                amount = 500000.0,
                isIncome = false,
                time = "00:00",
                icon = Icons.Default.CalendarToday,
                categoryColor = Color(0xFF3F51B5)
            ),
            Transaction(
                id = 3,
                title = "Trà sữa",
                category = "Ăn uống",
                amount = 40000.0,
                isIncome = false,
                time = "15:30",
                icon = Icons.Default.CalendarToday,
                categoryColor = Color(0xFF4CAF50)
            ),
            Transaction(
                id = 4,
                title = "Lương",
                category = "Lương",
                amount = 10000000.0,
                isIncome = true,
                time = "11:30",
                icon = Icons.Default.CalendarToday,
                categoryColor = Color(0xFFFFC107)
            )
        )
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header vàng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = headerColor,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.history_title),
                    fontSize = (20.sp * fontScale),
                    fontWeight = FontWeight.SemiBold,
                    color = headerTextColor
                )
            }

            // Date Picker
            Surface(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, borderColor),
                color = datecolor,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .offset(y = (-24).dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateFormatter.format(Date(selectedDate)),
                        fontSize = (16.sp * fontScale),
                        color = iconColor,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = stringResource(R.string.select_date),
                            tint = iconColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(-12.dp))

            Text(
                text = stringResource(R.string.transaction_list),
                fontWeight = FontWeight.Bold,
                fontSize = (18.sp * fontScale),
                color = textColor,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp)
            )

            // If empty
            if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_empty_state),
                        contentDescription = "Empty",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.no_transactions),
                        fontSize = (18.sp * fontScale),
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("add") },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "+ ",
                            fontSize = (18.sp * fontScale),
                            fontWeight = FontWeight.Bold,
                            color = buttonTextColor
                        )
                        Text(
                            text = stringResource(id = R.string.add_transaction),
                            fontSize = (15.sp * fontScale),
                            color = Color.White
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            fontScale = fontScale,
                            isDarkMode = isDarkMode,
                            onClick = {
                                navController.navigate("detail/${transaction.id}")
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDate = it }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK", color = Color(0xFF3C2E7E))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(id = R.string.cancel), color = Color.Gray)
                    }
                },
                colors = DatePickerDefaults.colors(containerColor = Color.White)
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

// ITEM CLICKABLE
@Composable
fun TransactionItem(
    transaction: Transaction,
    fontScale: Float = 1f,
    isDarkMode: Boolean = false,
    onClick: () -> Unit
) {
    val textColor = if (isDarkMode) Color.White else Color.Black
    val iconBgColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = transaction.icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                if (transaction.title.isNotEmpty()) {
                    Text(
                        text = transaction.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (15.sp * fontScale),
                        color = textColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = transaction.categoryColor,
                    modifier = Modifier.padding(top = if (transaction.title.isNotEmpty()) 4.dp else 0.dp)
                ) {
                    Text(
                        text = transaction.category,
                        color = if (transaction.isIncome) Color.Black else Color.White,
                        fontSize = (12.sp * fontScale),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = transaction.time,
                color = Color.Gray,
                fontSize = (12.sp * fontScale)
            )

            Text(
                text = (if (transaction.isIncome) "+" else "-") +
                        String.format("%,.0f", transaction.amount) + " đ",
                color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFE53935),
                fontSize = (15.sp * fontScale),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavHostController,
    transactionId: Int
) {
    val transactions = listOf(
        Transaction(1, "Bún bò", "Ăn uống", 30000.0, false, "15:30", Icons.Default.CalendarToday, Color(0xFF4CAF50)),
        Transaction(2, "Quần áo", "Mua sắm", 500000.0, false, "00:00", Icons.Filled.ShoppingCart, Color(0xFF3F51B5)),
        Transaction(3, "Trà sữa", "Ăn uống", 40000.0, false, "15:30", Icons.Default.CalendarToday, Color(0xFF4CAF50)),
        Transaction(4, "Lương", "Lương", 10000000.0, true, "11:30", Icons.Default.CalendarToday, Color(0xFFFFC107))
    )

    val transaction = transactions.firstOrNull { it.id == transactionId }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (transaction == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Không tìm thấy giao dịch!", color = Color.Red)
        }
        return
    }

    // Dialog xác nhận xóa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa giao dịch này?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Thực hiện xóa giao dịch ở đây
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Dialog sửa giao dịch
    if (showEditDialog) {
        EditTransactionDialog(
            transaction = transaction,
            onDismiss = { showEditDialog = false },
            onSave = { updatedTransaction ->
                // TODO: Thực hiện cập nhật giao dịch ở đây
                showEditDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFEB3B)
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { showEditDialog = true }) {
                        Text("Sửa", fontSize = 18.sp, color = Color(0xFF2196F3))
                    }
                    Text("|", color = Color.Gray, fontSize = 18.sp)
                    TextButton(onClick = { showDeleteDialog = true }) {
                        Text("Xóa", fontSize = 18.sp, color = Color.Red)
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            // --- Icon và tiêu đề ---
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF8E1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = transaction.icon,
                        contentDescription = null,
                        tint = transaction.categoryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))

            // ---- Các dòng thông tin ----
            DetailRow(label = "Kiểu", value = if (transaction.isIncome) "Thu nhập" else "Chi tiêu")
            DetailRow(label = "Số tiền", value = "%,d đ".format(transaction.amount.toInt()))
            DetailRow(label = "Thời gian", value = transaction.time)
            DetailRow(label = "Ghi chú", value = transaction.title)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {

        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    var amount by remember { mutableStateOf(transaction.amount.toString().replace(".0", "")) }
    var note by remember { mutableStateOf(transaction.title) }
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
                    Text("OK", color = Color(0xFFFFD600))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFFFFD600),
                    todayContentColor = Color(0xFFFFD600),
                    todayDateBorderColor = Color(0xFFFFD600)
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
            title = { Text("Chọn giờ") },
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
                    Text("Hủy", color = Color.Gray)
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
                            imageVector = transaction.icon,
                            contentDescription = transaction.category,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = transaction.category,
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
                        Text("Hủy", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val updatedTransaction = transaction.copy(
                                title = note,
                                amount = amount.toDoubleOrNull() ?: transaction.amount,
                                time = currentDateTime
                            )
                            onSave(updatedTransaction)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD600)
                        )
                    ) {
                        Text("Xác nhận", color = Color.Black)
                    }
                }
            }
        }
    }
}