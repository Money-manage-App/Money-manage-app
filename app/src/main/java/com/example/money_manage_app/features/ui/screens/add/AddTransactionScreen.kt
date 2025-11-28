package com.example.money_manage_app.features.ui.screens.add

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.money_manage_app.data.local.datastore.FontSizeManager
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import com.example.money_manage_app.data.local.datastore.ThemePreference
import com.example.money_manage_app.data.local.entity.CategoryEntity
import com.example.money_manage_app.features.navigation.Routes
import com.example.money_manage_app.features.viewmodel.CategoryViewModel
import com.example.money_manage_app.features.viewmodel.TransactionViewModel
import com.example.money_manage_app.features.viewmodel.UserViewModel
import kotlinx.coroutines.launch

private const val TAG = "AddTransaction"

// Helper: map icon name to ImageVector
fun getIconFromName(iconName: String): ImageVector {
    return when (iconName) {
        "Restaurant" -> Icons.Default.Restaurant
        "LocalBar" -> Icons.Default.LocalBar
        "Flight" -> Icons.Default.Flight
        "Movie" -> Icons.Default.Movie
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "LocalGasStation" -> Icons.Default.LocalGasStation
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "SportsSoccer" -> Icons.Default.SportsSoccer
        "EmojiFoodBeverage" -> Icons.Default.EmojiFoodBeverage
        "Checkroom" -> Icons.Default.Checkroom
        "Pets" -> Icons.Default.Pets
        "Build" -> Icons.Default.Build
        "CardGiftcard" -> Icons.Default.CardGiftcard
        "SportsEsports" -> Icons.Default.SportsEsports
        "DirectionsCar" -> Icons.Default.DirectionsCar
        "Security" -> Icons.Default.Security
        "Home" -> Icons.Default.Home
        "Receipt" -> Icons.Default.Receipt
        "Favorite" -> Icons.Default.Favorite
        "AttachMoney" -> Icons.Default.AttachMoney
        "Work" -> Icons.Default.Work
        "Star" -> Icons.Default.Star
        "AccountBalance" -> Icons.Default.AccountBalance
        "Savings" -> Icons.Default.Savings
        "TrendingUp" -> Icons.Default.TrendingUp
        "MilitaryTech" -> Icons.Default.MilitaryTech
        "AutoAwesome" -> Icons.Default.AutoAwesome
        "Money" -> Icons.Default.Money
        "Schedule" -> Icons.Default.Schedule
        "MoreHoriz" -> Icons.Default.MoreHoriz
        "CheckCircle" -> Icons.Default.CheckCircle
        else -> Icons.Default.Category
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavHostController,
    categoryViewModel: CategoryViewModel,
    userViewModel: UserViewModel,
    transactionViewModel: TransactionViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themePref = remember { ThemePreference(context) }
    val fontManager = remember { FontSizeManager(context) }
    val languagePref = remember { LanguagePreference(context) }

    val isDark by themePref.isDarkMode.collectAsState(initial = false)
    val fontScale by fontManager.fontSizeFlow.collectAsState(initial = 1f)
    val currentLanguage by languagePref.currentLanguage.collectAsState(initial = "Tiếng Việt")
    val isEnglish = currentLanguage == "English"
    val colors = MaterialTheme.colorScheme

    // ViewModel state
    val currentUserId by userViewModel.currentUserId.collectAsState()
    val expenseCategories by categoryViewModel.expenseCategories.collectAsState()
    val incomeCategories by categoryViewModel.incomeCategories.collectAsState()
    val isLoading by categoryViewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var showInputDialog by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Load user and set VMs
    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser(context)
    }
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            categoryViewModel.setUserId(currentUserId)
            categoryViewModel.loadCategories()
            transactionViewModel.setUserId(currentUserId)
        }
    }

    val categories = if (selectedTab == 0) expenseCategories else incomeCategories

    // Transaction input dialog
    if (showInputDialog && selectedCategory != null) {
        TransactionInputDialog(
            category = selectedCategory!!,
            isDark = isDark,
            fontScale = fontScale,
            isEnglish = isEnglish,
            onDismiss = {
                showInputDialog = false
                selectedCategory = null
            },
            onConfirm = { amountStr, note, dateTimeMillis ->
                scope.launch {
                    // Prevent double submit
                    if (isSubmitting) return@launch
                    isSubmitting = true

                    val cat = selectedCategory!!
                    Log.d(TAG, "Attempt add tx -> user=$currentUserId, catId=${cat.id}, cat.user=${cat.userId}")

                    // Basic validation
                    val amount = amountStr.toDoubleOrNull()
                    if (currentUserId.isEmpty()) {
                        errorMessage = if (isEnglish) "User not loaded" else "Người dùng chưa sẵn sàng"
                        isSubmitting = false
                        return@launch
                    }
                    if (amount == null || amount <= 0) {
                        errorMessage = if (isEnglish) "Invalid amount" else "Số tiền không hợp lệ"
                        isSubmitting = false
                        return@launch
                    }
                    if (cat.id <= 0) {
                        errorMessage = if (isEnglish) "Invalid category" else "Danh mục không hợp lệ"
                        isSubmitting = false
                        return@launch
                    }
                    if (cat.userId != currentUserId) {
                        errorMessage = if (isEnglish) "Category doesn't belong to user" else "Danh mục không thuộc người dùng"
                        isSubmitting = false
                        return@launch
                    }

                    try {
                        val success = transactionViewModel.addTransaction(
                            categoryId = cat.id,
                            amount = amount,
                            note = note,
                            date = dateTimeMillis,
                            isIncome = !cat.isExpense
                        )

                        Log.d(TAG, "Insert result = $success")
                        if (success) {
                            showSuccessMessage = true
                            kotlinx.coroutines.delay(400)
                            showSuccessMessage = false

                            // ✅ FIX: Just go back instead of creating new History screen
                            navController.navigateUp()
                        } else {
                            errorMessage = if (isEnglish) "Failed to add transaction" else "Thêm giao dịch thất bại"
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error adding transaction", e)
                        errorMessage = e.message ?: (if (isEnglish) "Error" else "Lỗi")
                    } finally {
                        isSubmitting = false
                        showInputDialog = false
                        selectedCategory = null
                    }
                }
            }
        )
    }

    // Main UI
    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Box(modifier = Modifier.fillMaxWidth().background(colors.primary).padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = {
                        // ✅ FIX: Simply go back to previous screen
                        navController.navigateUp()
                    }, modifier = Modifier.size(40.dp)) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.onPrimary)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = stringResource(R.string.add), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onPrimary)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Tabs
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 12.dp)) {
                TabButton(text = stringResource(R.string.expense), isSelected = selectedTab == 0, onClick = { selectedTab = 0 }, isDark = isDark, modifier = Modifier.weight(1f))
                TabButton(text = stringResource(R.string.income), isSelected = selectedTab == 1, onClick = { selectedTab = 1 }, isDark = isDark, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.primary)
                }
            } else if (categories.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = if (isEnglish) "No categories available" else "Không có danh mục", color = colors.onSurface.copy(alpha = 0.6f), fontSize = 14.sp)
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    items(categories) { category ->
                        CategoryItem(category = category, isSelected = false, isDark = isDark, isEnglish = isEnglish, onClick = {
                            Log.d(TAG, "Category clicked: ${category.nameVi} (ID: ${category.id})")
                            selectedCategory = category
                            showInputDialog = true
                        })
                    }
                }
            }
        }

        // Success snackbar
        if (showSuccessMessage) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
                Snackbar(containerColor = Color(0xFF4CAF50)) { Text(text = if (isEnglish) "Transaction added successfully!" else "Thêm giao dịch thành công!", color = Color.White) }
            }
        }

        // Error snackbar
        errorMessage?.let { msg ->
            LaunchedEffect(msg) {
                Log.e(TAG, "Error shown to user: $msg")
                kotlinx.coroutines.delay(3000)
                errorMessage = null
            }
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
                Snackbar(containerColor = Color(0xFFE53935)) { Text(text = msg, color = Color.White) }
            }
        }
    }
}

// Reused UI components: TabButton and CategoryItem (kept similar to your original)
@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(44.dp).padding(4.dp).then(if (isSelected) Modifier.background(if (isDark) Color(0xFF1E1E1E) else Color.White, RoundedCornerShape(4.dp)) else Modifier).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text = text, fontSize = 14.sp, color = if (isDark) Color.White else Color.Black, fontWeight = FontWeight.Normal)
    }
}

@Composable
fun CategoryItem(
    category: CategoryEntity,
    isSelected: Boolean,
    isDark: Boolean,
    isEnglish: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayName = when {
        !category.nameNote.isNullOrBlank() -> category.nameNote
        isEnglish -> category.nameEn
        else -> category.nameVi
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.clickable(onClick = onClick)) {
        Box(modifier = Modifier.size(48.dp).background(color = if (isSelected) Color(0xFFFFD600) else if (isDark) Color(0xFF2C2C2C) else Color.White, shape = CircleShape).border(width = if (isSelected) 0.dp else 1.dp, color = if (isDark) Color(0xFF404040) else Color(0xFFE0E0E0), shape = CircleShape), contentAlignment = Alignment.Center) {
            Icon(imageVector = getIconFromName(category.iconName), contentDescription = displayName, tint = if (isDark) Color.White else Color.Black, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = displayName, fontSize = 11.sp, color = if (isDark) Color.White else Color.Black, textAlign = TextAlign.Center, maxLines = 1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionInputDialog(
    category: CategoryEntity,
    isDark: Boolean,
    fontScale: Float,
    isEnglish: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedCalendar by remember { mutableStateOf(java.util.Calendar.getInstance()) }

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

    val categoryDisplayName = when {
        !category.nameNote.isNullOrBlank() -> category.nameNote
        isEnglish -> category.nameEn
        else -> category.nameVi
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedCalendar.timeInMillis)
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
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
            }) { Text("OK", color = if (isDark) Color.White else Color.Black) }
        }, dismissButton = {
            TextButton(onClick = { showDatePicker = false }) { Text(if (isEnglish) "Cancel" else "Hủy", color = if (isDark) Color.White else Color.Black) }
        }, colors = DatePickerDefaults.colors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White)) {
            DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White, titleContentColor = if (isDark) Color.White else Color.Black, headlineContentColor = if (isDark) Color.White else Color.Black, selectedDayContainerColor = Color(0xFFFEE912), selectedDayContentColor = Color.Black, todayContentColor = if (isDark) Color.White else Color.Black, todayDateBorderColor = Color.Transparent, dayContentColor = if (isDark) Color.White else Color.Black, weekdayContentColor = if (isDark) Color.White else Color.Black, yearContentColor = if (isDark) Color.White else Color.Black, currentYearContentColor = if (isDark) Color.White else Color.Black, selectedYearContainerColor = Color(0xFFFEE912), selectedYearContentColor = Color.Black, navigationContentColor = if (isDark) Color.White else Color.Black, dividerColor = Color.Transparent))
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = selectedCalendar.get(java.util.Calendar.HOUR_OF_DAY), initialMinute = selectedCalendar.get(java.util.Calendar.MINUTE), is24Hour = true)
        AlertDialog(onDismissRequest = { showTimePicker = false }, title = { Text(if (isEnglish) "Select time" else "Chọn giờ", color = if (isDark) Color.White else Color.Black) }, containerColor = if (isDark) Color.Black else Color.White, text = {
            TimePicker(state = timePickerState, colors = TimePickerDefaults.colors(clockDialColor = if (isDark) Color(0xFF2C2C2C) else Color(0xFFFFF9C4), selectorColor = Color(0xFFFFD600), timeSelectorSelectedContainerColor = Color(0xFFFFD600), periodSelectorSelectedContentColor = Color.Black, timeSelectorSelectedContentColor = Color.Black, clockDialSelectedContentColor = Color.Black))
        }, confirmButton = {
            TextButton(onClick = {
                val newCalendar = selectedCalendar.clone() as java.util.Calendar
                newCalendar.set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                newCalendar.set(java.util.Calendar.MINUTE, timePickerState.minute)
                selectedCalendar = newCalendar
                showTimePicker = false
            }) { Text("OK", color = Color(0xFFFFD600)) }
        }, dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text(if (isEnglish) "Cancel" else "Hủy", color = Color.Gray) } })
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFD600), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(imageVector = getIconFromName(category.iconName), contentDescription = categoryDisplayName, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = categoryDisplayName, fontSize = (18.sp * fontScale), fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth().background(if (isDark) Color(0xFF2C2C2C) else Color(0xFFF5F5F5), RoundedCornerShape(8.dp)).clickable { showDatePicker = true }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = "Time", tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = currentDateTime, fontSize = (14.sp * fontScale), color = if (isDark) Color.White else Color.Black, modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(value = amount, onValueChange = { amount = it }, modifier = Modifier.fillMaxWidth(), label = { Text(if (isEnglish) "Amount" else "Số tiền", color = if (isDark) Color.White else Color.Black) }, placeholder = { Text("0") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFFD600), unfocusedBorderColor = if (isDark) Color(0xFF404040) else Color(0xFFE0E0E0), focusedLabelColor = Color(0xFFFFD600), focusedTextColor = if (isDark) Color.White else Color.Black, unfocusedTextColor = if (isDark) Color.White else Color.Black), singleLine = true)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(value = note, onValueChange = { note = it }, modifier = Modifier.fillMaxWidth(), label = { Text(if (isEnglish) "Note" else "Ghi chú", color = if (isDark) Color.White else Color.Black) }, placeholder = { Text(if (isEnglish) "Enter note..." else "Nhập ghi chú...") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFFD600), unfocusedBorderColor = if (isDark) Color(0xFF404040) else Color(0xFFE0E0E0), focusedLabelColor = Color(0xFFFFD600), focusedTextColor = if (isDark) Color.White else Color.Black, unfocusedTextColor = if (isDark) Color.White else Color.Black), maxLines = 3)

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(if (isEnglish) "Cancel" else "Hủy", color = Color.Gray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (amount.isNotBlank()) {
                            Log.d(TAG, "Confirm clicked with amount: $amount, note: $note")
                            onConfirm(amount, note, selectedCalendar.timeInMillis)
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600)), enabled = amount.isNotBlank()) {
                        Text(if (isEnglish) "Confirm" else "Xác nhận", color = Color.Black)
                    }
                }
            }
        }
    }
}

// Waiting for final code generation in next update step.
