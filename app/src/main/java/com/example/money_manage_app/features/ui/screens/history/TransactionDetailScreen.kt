package com.example.money_manage_app.features.ui.screens.history

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.FontSizeManager
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import com.example.money_manage_app.data.local.datastore.ThemePreference
import com.example.money_manage_app.features.ui.screens.add.getIconFromName
import com.example.money_manage_app.features.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavHostController,
    transactionId: Int,
    transactionViewModel: TransactionViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fontSizeManager = remember { FontSizeManager(context) }
    val fontScale by fontSizeManager.fontSizeFlow.collectAsState(initial = 1f)

    val themePreference = remember { ThemePreference(context) }
    val isDarkMode by themePreference.isDarkMode.collectAsState(initial = false)

    val languagePreference = remember { LanguagePreference(context) }
    val currentLanguage by languagePreference.currentLanguage.collectAsState(initial = "Tiếng Việt")
    val isEnglish = currentLanguage == "English"

    val transactionWithCategory by transactionViewModel.selectedTransaction.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // ✅ Load transaction detail
    LaunchedEffect(transactionId) {
        transactionViewModel.loadTransactionById(transactionId)
    }

    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    // ✅ Kiểm tra nếu transaction null
    if (transactionWithCategory == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFFFEB3B))
        }
        return
    }

    val transaction = transactionWithCategory!!.transaction
    val category = transactionWithCategory!!.category

    // ✅ Tên category theo ngôn ngữ
    val categoryName = when {
        !category.nameNote.isNullOrBlank() -> category.nameNote!!
        isEnglish -> category.nameEn ?: category.nameVi
        else -> category.nameVi
    }

    // ✅ Dialog xác nhận xóa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    if (isEnglish) "Confirm Delete" else "Xác nhận xóa",
                    color = textColor
                )
            },
            text = {
                Text(
                    if (isEnglish) "Are you sure you want to delete this transaction?"
                    else "Bạn có chắc chắn muốn xóa giao dịch này?",
                    color = textColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val success = transactionViewModel.deleteTransaction(transaction)
                            showDeleteDialog = false
                            if (success) {
                                navController.popBackStack()
                            }
                        }
                    }
                ) {
                    Text(if (isEnglish) "Delete" else "Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(if (isEnglish) "Cancel" else "Hủy", color = textColor)
                }
            },
            containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
        )
    }

    // ✅ Dialog sửa giao dịch
    if (showEditDialog) {
        EditTransactionDialog(
            transaction = transaction,
            category = category,
            isDarkMode = isDarkMode,
            isEnglish = isEnglish,
            onDismiss = { showEditDialog = false },
            onSave = { amount, note, dateTimeMillis ->
                scope.launch {
                    val success = transactionViewModel.updateTransaction(
                        id = transaction.id,
                        categoryId = transaction.categoryId,
                        amount = amount.toDoubleOrNull() ?: transaction.amount,
                        note = note,
                        date = dateTimeMillis,
                        isIncome = transaction.isIncome
                    )
                    showEditDialog = false
                    if (success) {
                        // Reload transaction detail
                        transactionViewModel.loadTransactionById(transactionId)
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEnglish) "Detail" else "Chi tiết",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFEB3B)
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { showEditDialog = true }) {
                        Text(
                            if (isEnglish) "Edit" else "Sửa",
                            fontSize = 18.sp,
                            color = Color(0xFF2196F3)
                        )
                    }
                    Text("|", color = Color.Gray, fontSize = 18.sp)
                    TextButton(onClick = { showDeleteDialog = true }) {
                        Text(
                            if (isEnglish) "Delete" else "Xóa",
                            fontSize = 18.sp,
                            color = Color.Red
                        )
                    }
                }
            }
        },
        containerColor = backgroundColor
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
                        imageVector = getIconFromName(category.iconName),
                        contentDescription = null,
                        tint = if (transaction.isIncome) Color(0xFFFFC107) else Color(0xFF2196F3),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            Spacer(Modifier.height(32.dp))

            // ---- Các dòng thông tin ----
            DetailRow(
                label = if (isEnglish) "Type" else "Kiểu",
                value = if (transaction.isIncome) {
                    if (isEnglish) "Income" else "Thu nhập"
                } else {
                    if (isEnglish) "Expense" else "Chi tiêu"
                },
                textColor = textColor
            )

            DetailRow(
                label = if (isEnglish) "Amount" else "Số tiền",
                value = "%,d đ".format(transaction.amount.toInt()),
                textColor = textColor
            )

            DetailRow(
                label = if (isEnglish) "Date & Time" else "Thời gian",
                value = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(transaction.date)),
                textColor = textColor
            )

            DetailRow(
                label = if (isEnglish) "Note" else "Ghi chú",
                value = transaction.note.ifEmpty { if (isEnglish) "No note" else "Không có ghi chú" },
                textColor = textColor
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, textColor: Color) {
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
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(
    transaction: com.example.money_manage_app.data.local.entity.TransactionEntity,
    category: com.example.money_manage_app.data.local.entity.CategoryEntity,
    isDarkMode: Boolean,
    isEnglish: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, Long) -> Unit
) {
    var amount by remember { mutableStateOf(transaction.amount.toString().replace(".0", "")) }
    var note by remember { mutableStateOf(transaction.note) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var selectedCalendar by remember {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = transaction.date
        mutableStateOf(cal)
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

    val categoryName = when {
        !category.nameNote.isNullOrBlank() -> category.nameNote!!
        isEnglish -> category.nameEn ?: category.nameVi
        else -> category.nameVi
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
                    Text("OK", color = if (isDarkMode) Color.White else Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(if (isEnglish) "Cancel" else "Hủy", color = if (isDarkMode) Color.White else Color.Black)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
                    titleContentColor = if (isDarkMode) Color.White else Color.Black,
                    headlineContentColor = if (isDarkMode) Color.White else Color.Black,
                    selectedDayContainerColor = Color(0xFFFEE912),
                    selectedDayContentColor = Color.Black,
                    todayContentColor = if (isDarkMode) Color.White else Color.Black,
                    todayDateBorderColor = Color.Transparent,
                    dayContentColor = if (isDarkMode) Color.White else Color.Black,
                    weekdayContentColor = if (isDarkMode) Color.White else Color.Black,
                    yearContentColor = if (isDarkMode) Color.White else Color.Black,
                    currentYearContentColor = if (isDarkMode) Color.White else Color.Black,
                    selectedYearContainerColor = Color(0xFFFEE912),
                    selectedYearContentColor = Color.Black,
                    navigationContentColor = if (isDarkMode) Color.White else Color.Black,
                    dividerColor = Color.Transparent
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
            title = { Text(if (isEnglish) "Select time" else "Chọn giờ", color = if (isDarkMode) Color.White else Color.Black) },
            containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFFFF9C4),
                        selectorColor = Color(0xFFFFD600),
                        timeSelectorSelectedContainerColor = Color(0xFFFFD600),
                        periodSelectorSelectedContentColor = Color.Black,
                        timeSelectorSelectedContentColor = Color.Black,
                        clockDialSelectedContentColor = Color.Black
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
                    Text(if (isEnglish) "Cancel" else "Hủy", color = Color.Gray)
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
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
            )
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
                            imageVector = getIconFromName(category.iconName),
                            contentDescription = categoryName,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = categoryName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFF5F5F5),
                            RoundedCornerShape(8.dp)
                        )
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
                        color = if (isDarkMode) Color.White else Color.Black,
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
                    label = { Text(if (isEnglish) "Amount" else "Số tiền", color = if (isDarkMode) Color.White else Color.Black) },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD600),
                        unfocusedBorderColor = if (isDarkMode) Color(0xFF404040) else Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFFD600),
                        focusedTextColor = if (isDarkMode) Color.White else Color.Black,
                        unfocusedTextColor = if (isDarkMode) Color.White else Color.Black
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(if (isEnglish) "Note" else "Ghi chú", color = if (isDarkMode) Color.White else Color.Black) },
                    placeholder = { Text(if (isEnglish) "Enter note..." else "Nhập ghi chú...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD600),
                        unfocusedBorderColor = if (isDarkMode) Color(0xFF404040) else Color(0xFFE0E0E0),
                        focusedLabelColor = Color(0xFFFFD600),
                        focusedTextColor = if (isDarkMode) Color.White else Color.Black,
                        unfocusedTextColor = if (isDarkMode) Color.White else Color.Black
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (isEnglish) "Cancel" else "Hủy", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (amount.isNotBlank()) {
                                onSave(amount, note, selectedCalendar.timeInMillis)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD600)
                        ),
                        enabled = amount.isNotBlank()
                    ) {
                        Text(if (isEnglish) "Confirm" else "Xác nhận", color = Color.Black)
                    }
                }
            }
        }
    }
}