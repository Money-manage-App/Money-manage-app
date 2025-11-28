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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.money_manage_app.data.local.entity.TransactionWithCategory
import com.example.money_manage_app.features.ui.screens.add.getIconFromName
import com.example.money_manage_app.features.viewmodel.TransactionViewModel
import com.example.money_manage_app.features.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val fontSizeManager = remember { FontSizeManager(context) }
    val fontScale by fontSizeManager.fontSizeFlow.collectAsState(initial = 1f)

    val themePreference = remember { ThemePreference(context) }
    val isDarkMode by themePreference.isDarkMode.collectAsState(initial = false)

    val languagePreference = remember { LanguagePreference(context) }
    val currentLanguage by languagePreference.currentLanguage.collectAsState(initial = "Tiếng Việt")
    val isEnglish = currentLanguage == "English"

    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val headerColor = Color(0xFFFEE912)
    val headerTextColor = Color.Black
    val textColor = if (isDarkMode) Color.White else Color.Black
    val borderColor = if (isDarkMode) Color(0xFF3E3E3E) else Color.LightGray
    val buttonColor = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFF4A4A4A)
    val buttonTextColor = Color(0xFFFEE912)
    val iconColor = if (isDarkMode) Color.White else Color.Black
    val datecolor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White
    val datePickerContainerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val datePickerTextColor = if (isDarkMode) Color.White else Color.Black
    val datePickerButtonColor = if (isDarkMode) Color.White else Color.Black

    // ✅ Load userId và transactions
    val currentUserId by userViewModel.currentUserId.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val isLoading by transactionViewModel.isLoading.collectAsState()

    // ✅ Chỉ load user một lần khi màn hình khởi động
    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser(context)
    }

    // ✅ Load transactions khi có userId (chỉ một lần)
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            transactionViewModel.setUserId(currentUserId)
        }
    }

    // Date picker state
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())

    // ✅ Filter transactions by selected date - sử dụng derivedStateOf để tránh recompose
    val filteredTransactions by remember {
        derivedStateOf {
            val startOfDay = transactionViewModel.getStartOfDay(selectedDate)
            val endOfDay = transactionViewModel.getEndOfDay(selectedDate)

            transactions.filter { transactionWithCategory ->
                val transactionDate = transactionWithCategory.transaction.date
                transactionDate in startOfDay..endOfDay
            }
        }
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

            // ✅ Loading state
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = headerColor)
                }
            }
            // ✅ Empty state
            else if (filteredTransactions.isEmpty()) {
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
                        onClick = { navController.navigate("add_transaction") },
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
            }
            // ✅ Transactions list - FIX: Sử dụng transaction.id làm key
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    items(
                        items = filteredTransactions,
                        key = { it.transaction.id } // ✅ FIX: Sử dụng unique ID
                    ) { transactionWithCategory ->
                        TransactionItem(
                            transactionWithCategory = transactionWithCategory,
                            fontScale = fontScale,
                            isDarkMode = isDarkMode,
                            isEnglish = isEnglish,
                            onClick = {
                                navController.navigate("detail/${transactionWithCategory.transaction.id}")
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // DatePicker Dialog
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
                        Text("OK", color = datePickerButtonColor)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(id = R.string.cancel), color = datePickerButtonColor)
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = datePickerContainerColor
                )
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = datePickerContainerColor,
                        titleContentColor = datePickerTextColor,
                        headlineContentColor = datePickerTextColor,
                        selectedDayContainerColor = Color(0xFFFEE912),
                        selectedDayContentColor = Color.Black,
                        todayContentColor = datePickerTextColor,
                        todayDateBorderColor = Color.Transparent,
                        dayContentColor = datePickerTextColor,
                        weekdayContentColor = datePickerTextColor,
                        yearContentColor = datePickerTextColor,
                        currentYearContentColor = datePickerTextColor,
                        selectedYearContainerColor = Color(0xFFFEE912),
                        selectedYearContentColor = Color.Black,
                        navigationContentColor = datePickerTextColor,
                        dividerColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    transactionWithCategory: TransactionWithCategory,
    fontScale: Float = 1f,
    isDarkMode: Boolean = false,
    isEnglish: Boolean = false,
    onClick: () -> Unit
) {
    val transaction = transactionWithCategory.transaction
    val category = transactionWithCategory.category

    val textColor = if (isDarkMode) Color.White else Color.Black
    val iconBgColor = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFF5F5F5)

    // ✅ Tên category theo ngôn ngữ
    val categoryName = when {
        !category.nameNote.isNullOrBlank() -> category.nameNote!!
        isEnglish -> category.nameEn ?: category.nameVi
        else -> category.nameVi
    }

    // ✅ Format time
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeString = timeFormat.format(Date(transaction.date))

    // ✅ Màu category
    val categoryColor = if (transaction.isIncome) {
        Color(0xFFFFC107) // Vàng cho thu nhập
    } else {
        Color(0xFF2196F3) // Xanh cho chi tiêu
    }

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
                    .background(iconBgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconFromName(category.iconName),
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                if (transaction.note.isNotEmpty()) {
                    Text(
                        text = transaction.note,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (15.sp * fontScale),
                        color = textColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = categoryColor,
                    modifier = Modifier.padding(top = if (transaction.note.isNotEmpty()) 4.dp else 0.dp)
                ) {
                    Text(
                        text = categoryName,
                        color = Color.White,
                        fontSize = (12.sp * fontScale),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = timeString,
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