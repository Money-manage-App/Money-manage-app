package com.example.money_manage_app.features.ui.screens.report

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.ThemePreference
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import com.example.money_manage_app.data.local.datastore.FontSizeManager
import com.example.money_manage_app.features.viewmodel.TransactionViewModel
import com.example.money_manage_app.features.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportScreen(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currencyFormatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    // User ID
    val currentUserId by userViewModel.currentUserId.collectAsState()

    // State cho DateRangePicker
    var startDate by remember { mutableStateOf(calendar.apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }.timeInMillis) }
    var endDate by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startDate,
        initialSelectedEndDateMillis = endDate
    )

    // 🎨 Lấy theme preference (dark mode)
    val themePref = remember { ThemePreference(context) }
    val isDarkMode by themePref.isDarkMode.collectAsState(initial = false)

    // 🌍 Lấy ngôn ngữ
    val langPref = remember { LanguagePreference(context) }
    val currentLanguage by langPref.currentLanguage.collectAsState(initial = "Tiếng Việt")

    // 🔍 Lấy font scale
    val fontManager = remember { FontSizeManager(context) }
    val fontScale by fontManager.fontSizeFlow.collectAsState(initial = 1f)

    // ✅ State cho báo cáo tài chính
    var totalIncome by remember { mutableStateOf(0.0) }
    var totalExpense by remember { mutableStateOf(0.0) }
    var isLoadingData by remember { mutableStateOf(false) }

    // ✅ Load dữ liệu khi userId hoặc date range thay đổi
    LaunchedEffect(currentUserId, startDate, endDate) {
        if (currentUserId.isNotEmpty()) {
            isLoadingData = true
            scope.launch {
                try {
                    val startOfDay = transactionViewModel.getStartOfDay(startDate)
                    val endOfDay = transactionViewModel.getEndOfDay(endDate)

                    // Lấy tổng thu nhập và chi tiêu từ repository
                    totalIncome = transactionViewModel.getTotalIncome(currentUserId, startOfDay, endOfDay)
                    totalExpense = transactionViewModel.getTotalExpense(currentUserId, startOfDay, endOfDay)
                } catch (e: Exception) {
                    android.util.Log.e("ReportScreen", "Error loading data", e)
                } finally {
                    isLoadingData = false
                }
            }
        }
    }

    // Tính toán
    val totalBalance = totalIncome - totalExpense
    val daysBetween = kotlin.math.max(1, ((endDate - startDate) / (24 * 60 * 60 * 1000)) + 1)
    val avgExpensePerDay = totalExpense / daysBetween
    val avgIncomePerDay = totalIncome / daysBetween

    // Màu sắc theo theme
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFF8F8F8)
    val cardBackground = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val dateBoxColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color.Black
    val textSecondary = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF666666)
    val expenseBackground = if (isDarkMode) Color(0xFF4D2020) else Color(0xFFFFEBEE)
    val incomeBackground = if (isDarkMode) Color(0xFF1B3A1B) else Color(0xFFE8F5E9)
    val expenseTextColor = if (isDarkMode) Color(0xFFFF6B6B) else Color(0xFFE53935)
    val incomeTextColor = if (isDarkMode) Color(0xFF66BB6A) else Color(0xFF43A047)

    // Màu cho DatePicker theo theme
    val datePickerContainerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val datePickerTextColor = if (isDarkMode) Color.White else Color.Black
    val datePickerButtonColor = if (isDarkMode) Color.White else Color.Black
    val datePickerHeadlineColor = if (isDarkMode) Color.White else Color.Black

    // 🌍 Text đa ngôn ngữ từ string resources
    val reportTitle = stringResource(R.string.report)
    val selectDateRange = stringResource(R.string.report_date_range)
    val totalBalanceText = stringResource(R.string.total_balance)
    val expense = stringResource(R.string.expense)
    val income = stringResource(R.string.income)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // ✅ Header màu vàng với title và date picker
        Surface(
            color = Color(0xFFFEE912),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                // Title
                Text(
                    text = reportTitle,
                    fontSize = (24.sp * fontScale),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date picker trong header
                Surface(
                    shape = RoundedCornerShape(30.dp),
                    color = dateBoxColor,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable { showDateRangePicker = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${dateFormatter.format(Date(startDate))} - ${dateFormatter.format(Date(endDate))}",
                            fontSize = (16.sp * fontScale),
                            fontWeight = FontWeight.Medium,
                            color = textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ Card tổng và chi tiết với dữ liệu thực
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            if (isLoadingData) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tổng số dư
                    Text(
                        totalBalanceText,
                        fontSize = (18.sp * fontScale),
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${currencyFormatter.format(totalBalance)} đ",
                        color = if (totalBalance >= 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                        fontSize = (26.sp * fontScale),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Chi tiêu
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(expenseBackground, RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                expense,
                                fontWeight = FontWeight.Bold,
                                fontSize = (15.sp * fontScale),
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "${currencyFormatter.format(totalExpense)} đ",
                                color = expenseTextColor,
                                fontSize = (20.sp * fontScale),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                stringResource(
                                    R.string.average_per_day,
                                    currencyFormatter.format(avgExpensePerDay)
                                ),
                                fontSize = (11.sp * fontScale),
                                color = textSecondary
                            )
                        }

                        // Thu nhập
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(incomeBackground, RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                income,
                                fontWeight = FontWeight.Bold,
                                fontSize = (15.sp * fontScale),
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "${currencyFormatter.format(totalIncome)} đ",
                                color = incomeTextColor,
                                fontSize = (20.sp * fontScale),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                stringResource(
                                    R.string.average_per_day,
                                    currencyFormatter.format(avgIncomePerDay)
                                ),
                                fontSize = (11.sp * fontScale),
                                color = textSecondary
                            )
                        }
                    }
                }
            }
        }
    }

    // DateRangePicker Dialog - Chọn 2 ngày theo theme
    if (showDateRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateRangePickerState.selectedStartDateMillis?.let {
                            startDate = it
                        }
                        dateRangePickerState.selectedEndDateMillis?.let {
                            endDate = it
                        }
                        showDateRangePicker = false
                    }
                ) {
                    Text("OK", color = datePickerButtonColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text(
                        text = if (currentLanguage == "English") "Cancel" else "Hủy",
                        color = datePickerButtonColor
                    )
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = datePickerContainerColor
            )
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = if (currentLanguage == "English") "Select date range" else "Chọn khoảng thời gian",
                        modifier = Modifier.padding(16.dp),
                        color = datePickerTextColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                headline = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (currentLanguage == "English") "From" else "Từ ngày",
                                fontSize = 12.sp,
                                color = datePickerTextColor.copy(alpha = 0.6f)
                            )
                            Text(
                                text = dateRangePickerState.selectedStartDateMillis?.let {
                                    dateFormatter.format(Date(it))
                                } ?: "--/--/----",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = datePickerHeadlineColor
                            )
                        }
                        Column {
                            Text(
                                text = if (currentLanguage == "English") "To" else "Đến ngày",
                                fontSize = 12.sp,
                                color = datePickerTextColor.copy(alpha = 0.6f)
                            )
                            Text(
                                text = dateRangePickerState.selectedEndDateMillis?.let {
                                    dateFormatter.format(Date(it))
                                } ?: "--/--/----",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = datePickerHeadlineColor
                            )
                        }
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = datePickerContainerColor,
                    titleContentColor = datePickerTextColor,
                    headlineContentColor = datePickerHeadlineColor,
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
                    dividerColor = Color.Transparent,
                    dayInSelectionRangeContainerColor = Color(0xFFFEE912).copy(alpha = 0.3f),
                    dayInSelectionRangeContentColor = Color.Black
                )
            )
        }
    }
}