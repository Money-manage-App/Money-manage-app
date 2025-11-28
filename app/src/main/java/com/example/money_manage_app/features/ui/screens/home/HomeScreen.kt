package com.example.money_manage_app.features.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.ThemePreference
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import com.example.money_manage_app.features.viewmodel.TransactionViewModel
import com.example.money_manage_app.features.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themePreference = remember { ThemePreference(context) }
    val languagePreference = remember { LanguagePreference(context) }

    val isDarkMode by themePreference.isDarkMode.collectAsState(initial = false)
    val currentLanguage by languagePreference.currentLanguage.collectAsState(initial = "Tiếng Việt")
    val currentUserId by userViewModel.currentUserId.collectAsState()
    val currencyFormatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    // State cho chọn tháng
    val calendar = Calendar.getInstance()
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var showMonthPicker by remember { mutableStateOf(false) }
    val monthFormatter = SimpleDateFormat("MM/yyyy", Locale.getDefault())

    // ✅ STATE CHO DỮ LIỆU
    var totalBalance by remember { mutableStateOf(0.0) }
    var monthlyIncome by remember { mutableStateOf(0.0) }
    var monthlyExpense by remember { mutableStateOf(0.0) }
    var isLoadingData by remember { mutableStateOf(false) }

    // ✅ LOAD TỔNG SỐ DƯ (ĐẾN HIỆN TẠI)
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            scope.launch {
                try {
                    val startOfTime = 0L
                    val currentTime = System.currentTimeMillis()

                    val totalIncome = transactionViewModel.getTotalIncome(currentUserId, startOfTime, currentTime)
                    val totalExpense = transactionViewModel.getTotalExpense(currentUserId, startOfTime, currentTime)

                    totalBalance = totalIncome - totalExpense
                } catch (e: Exception) {
                    android.util.Log.e("HomeScreen", "Error loading total balance", e)
                }
            }
        }
    }

    // ✅ LOAD DỮ LIỆU THÁNG
    LaunchedEffect(currentUserId, selectedMonth, selectedYear) {
        if (currentUserId.isNotEmpty()) {
            isLoadingData = true
            scope.launch {
                try {
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.YEAR, selectedYear)
                    cal.set(Calendar.MONTH, selectedMonth)

                    // Lấy ngày đầu tháng
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    val startOfMonth = cal.timeInMillis

                    // Lấy ngày cuối tháng
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                    cal.set(Calendar.HOUR_OF_DAY, 23)
                    cal.set(Calendar.MINUTE, 59)
                    cal.set(Calendar.SECOND, 59)
                    cal.set(Calendar.MILLISECOND, 999)
                    val endOfMonth = cal.timeInMillis

                    monthlyIncome = transactionViewModel.getTotalIncome(currentUserId, startOfMonth, endOfMonth)
                    monthlyExpense = transactionViewModel.getTotalExpense(currentUserId, startOfMonth, endOfMonth)

                } catch (e: Exception) {
                    android.util.Log.e("HomeScreen", "Error loading monthly data", e)
                } finally {
                    isLoadingData = false
                }
            }
        }
    }

    // Định nghĩa màu sắc theo theme
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val topBarColor = Color(0xFFFFEB3B)
    val cardBackgroundColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.Black
    val textPrimaryColor = if (isDarkMode) Color.White else Color.Black
    val textOnCardColor = Color.White
    val borderColor = if (isDarkMode) Color(0xFF404040) else Color.Gray
    val dateBoxBackground = if (isDarkMode) Color(0xFF2C2C2C) else Color.White
    val expenseBoxColor = Color(0xFFFFEB3B)
    val incomeBoxColor = Color(0xFFF5F5F5)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- Thanh trên ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(topBarColor)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = stringResource(R.string.home_title),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 8.dp)
            )
        }

        // --- Khung thông tin tổng quan ---
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(130.dp)
                .padding(top = 50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(cardBackgroundColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(0.9f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.total_balance))
                    append(" ")
                    withStyle(
                        style = SpanStyle(
                            color = if (totalBalance >= 0) Color.Yellow else Color.Red
                        )
                    ) {
                        append("${currencyFormatter.format(totalBalance)} đ")
                    }
                },
                color = textOnCardColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
            )
        }

        // --- Nội dung chính ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 135.dp, start = 12.dp, end = 12.dp)
        ) {
            // --- Chọn tháng ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .background(dateBoxBackground)
                    .clickable { showMonthPicker = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format("%02d/%d", selectedMonth + 1, selectedYear),
                    fontSize = 16.sp,
                    color = textPrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Hai ô Chi tiêu / Thu nhập ---
            if (isLoadingData) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = topBarColor)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(expenseBoxColor)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.expense),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "${currencyFormatter.format(monthlyExpense)} đ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(incomeBoxColor)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.income),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "${currencyFormatter.format(monthlyIncome)} đ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- Ghi chú màu ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(Color(0xFF607D8B)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.income),
                        color = Color(0xFF607D8B),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(Color(0xFFFF9800)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.expense),
                        color = Color(0xFFFF9800),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // --- Biểu đồ tròn ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                if (monthlyIncome == 0.0 && monthlyExpense == 0.0) {
                    Text(
                        text = if (currentLanguage == "English")
                            "No data for this month"
                        else
                            "Chưa có dữ liệu tháng này",
                        color = textPrimaryColor.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                } else {
                    DonutChart(
                        data = listOf(monthlyIncome.toFloat(), monthlyExpense.toFloat()),
                        colors = listOf(Color(0xFF607D8B), Color(0xFFFF9800)),
                        backgroundColor = backgroundColor
                    )
                }
            }
        }

        // Month Picker Dialog
        if (showMonthPicker) {
            MonthPickerDialog(
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                onDismiss = { showMonthPicker = false },
                onMonthSelected = { month, year ->
                    selectedMonth = month
                    selectedYear = year
                    showMonthPicker = false
                },
                isDarkMode = isDarkMode,
                currentLanguage = currentLanguage
            )
        }
    }
}

@Composable
fun MonthPickerDialog(
    selectedMonth: Int,
    selectedYear: Int,
    onDismiss: () -> Unit,
    onMonthSelected: (Int, Int) -> Unit,
    isDarkMode: Boolean,
    currentLanguage: String
) {
    val months = if (currentLanguage == "English") {
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    } else {
        listOf("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12")
    }

    var currentYear by remember { mutableStateOf(selectedYear) }
    var tempSelectedMonth by remember { mutableStateOf(selectedMonth) }
    var showYearPicker by remember { mutableStateOf(false) }
    var yearPageStart by remember { mutableStateOf(2000) }

    val dialogBackgroundColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = dialogBackgroundColor,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (showYearPicker) {
                        // Ở màn hình chọn năm: next danh sách năm (lùi 20 năm)
                        yearPageStart = (yearPageStart - 20).coerceAtLeast(1900)
                    } else {
                        // Ở màn hình chọn tháng: next năm (lùi 1 năm)
                        currentYear--
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = if (showYearPicker) "Danh sách năm trước" else "Năm trước",
                        tint = textColor
                    )
                }
                Text(
                    text = if (showYearPicker) {
                        "$yearPageStart - ${yearPageStart + 19}"
                    } else {
                        currentYear.toString()
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.clickable {
                        showYearPicker = !showYearPicker
                        if (showYearPicker) {
                            // Tính toán trang năm hiện tại
                            yearPageStart = (currentYear / 20) * 20
                        }
                    }
                )
                IconButton(onClick = {
                    if (showYearPicker) {
                        // Ở màn hình chọn năm: next danh sách năm (tiến 20 năm)
                        yearPageStart = (yearPageStart + 20).coerceAtMost(2080)
                    } else {
                        // Ở màn hình chọn tháng: next năm (tiến 1 năm)
                        currentYear++
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = if (showYearPicker) "Danh sách năm sau" else "Năm sau",
                        tint = textColor
                    )
                }
            }
        },
        text = {
            if (showYearPicker) {
                // Year Picker - Hiển thị 20 năm mỗi trang
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    val years = (yearPageStart until yearPageStart + 20).toList()

                    // Tạo grid 4 cột x 5 hàng
                    for (rowIndex in 0 until 5) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (colIndex in 0..3) {
                                val yearIndex = rowIndex * 4 + colIndex
                                if (yearIndex < years.size) {
                                    val year = years[yearIndex]
                                    val isSelected = year == currentYear

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isSelected) Color(0xFFFFEB3B) else Color.Transparent
                                            )
                                            .clickable {
                                                currentYear = year
                                                showYearPicker = false
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = year.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.Black else textColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Month Picker
                Column {
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0..3) {
                                val monthIndex = row * 4 + col
                                val isSelected = monthIndex == tempSelectedMonth

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) Color(0xFFFFEB3B) else Color.Transparent
                                        )
                                        .clickable { tempSelectedMonth = monthIndex }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = months[monthIndex],
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.Black else textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onMonthSelected(tempSelectedMonth, currentYear)
            }) {
                Text(
                    text = "OK",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (currentLanguage == "English") "Cancel" else "Hủy",
                    color = textColor
                )
            }
        }
    )
}

@Composable
fun DonutChart(
    data: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    holeRatio: Float = 0.6f,
    backgroundColor: Color = Color.White
) {
    val total = data.sum()
    if (total == 0f) return

    val angles = data.map { 360f * it / total }

    Box(modifier = modifier.size(180.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.minDimension * (1 - holeRatio) / 2
            var startAngle = -90f
            for (i in data.indices) {
                drawArc(
                    color = colors.getOrElse(i) { Color.Gray },
                    startAngle = startAngle,
                    sweepAngle = angles[i],
                    useCenter = false,
                    size = Size(size.width, size.height),
                    style = Stroke(width = strokeWidth)
                )
                startAngle += angles[i]
            }

            // Lỗ tròn ở giữa
            drawCircle(
                color = backgroundColor,
                radius = size.minDimension * holeRatio / 2
            )
        }

        // Hiển thị phần trăm
        val radius = 130f
        var startAngle = -90f
        for (i in data.indices) {
            val midAngle = startAngle + angles[i] / 2
            val x = (radius * cos(Math.toRadians(midAngle.toDouble()))).toFloat()
            val y = (radius * sin(Math.toRadians(midAngle.toDouble()))).toFloat()

            val percentage = (data[i] / total * 100)
            Text(
                text = String.format("%.2f%%", percentage),
                color = colors.getOrElse(i) { Color.Gray },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x.dp, y.dp)
            )

            startAngle += angles[i]
        }
    }
}