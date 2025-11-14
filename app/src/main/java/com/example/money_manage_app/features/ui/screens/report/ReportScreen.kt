package com.example.money_manage_app.features.ui.screens.report

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
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
import com.example.money_manage_app.features.common.components.YellowHeader
import com.example.money_manage_app.data.local.datastore.ThemePreference
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import com.example.money_manage_app.data.local.datastore.FontSizeManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportScreen(navController: NavHostController) {

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var selectingStart by remember { mutableStateOf(true) }

    // 🎨 Lấy theme preference (dark mode)
    val themePref = remember { ThemePreference(context) }
    val isDarkMode by themePref.isDarkMode.collectAsState(initial = false)

    // 🌐 Lấy ngôn ngữ
    val langPref = remember { LanguagePreference(context) }
    val currentLanguage by langPref.currentLanguage.collectAsState(initial = "Tiếng Việt")

    // 📏 Lấy font scale
    val fontManager = remember { FontSizeManager(context) }
    val fontScale by fontManager.fontSizeFlow.collectAsState(initial = 1f)

    // Màu sắc theo theme
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFF8F8F8)
    val cardBackground = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color.Black
    val textSecondary = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF666666)
    val expenseBackground = if (isDarkMode) Color(0xFF4D2020) else Color(0xFFFFEBEE)
    val incomeBackground = if (isDarkMode) Color(0xFF1B3A1B) else Color(0xFFE8F5E9)
    val expenseTextColor = if (isDarkMode) Color(0xFFFF6B6B) else Color(0xFFE53935)
    val incomeTextColor = if (isDarkMode) Color(0xFF66BB6A) else Color(0xFF43A047)

    // 🌐 Text đa ngôn ngữ từ string resources
    val reportTitle = stringResource(R.string.report)
    val selectDateRange = stringResource(R.string.report_date_range)
    val totalBalance = stringResource(R.string.total_balance)
    val expense = stringResource(R.string.expense)
    val income = stringResource(R.string.income)

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
            .background(backgroundColor)
    ) {
        // ✅ Header màu vàng với title và date picker
        Surface(
            color = Color(0xFFFDD835),
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
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable {
                            if (startDate == null) {
                                selectingStart = true
                                showDatePicker { date ->
                                    startDate = date
                                }
                            } else {
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
                            .padding(horizontal = 20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = when {
                                startDate == null -> selectDateRange
                                endDate == null -> "${dateFormatter.format(startDate!!)} - ..."
                                else -> "${dateFormatter.format(startDate!!)} - ${dateFormatter.format(endDate!!)}"
                            },
                            fontSize = (16.sp * fontScale),
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card tổng và chi tiết
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tổng số dư
                Text(
                    totalBalance,
                    fontSize = (16.sp * fontScale),
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "xxx.xxx.xxx đ",
                    color = Color(0xFF4CAF50),
                    fontSize = (26.sp * fontScale),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Chi tiêu
                    Column(
                        modifier = Modifier
                            .weight(1f)
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
                            "x.xxx.xxx đ",
                            color = expenseTextColor,
                            fontSize = (20.sp * fontScale),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.average_per_day, "xxx.xxx"),
                            fontSize = (11.sp * fontScale),
                            color = textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Thu nhập
                    Column(
                        modifier = Modifier
                            .weight(1f)
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
                            "x.xxx.xxx đ",
                            color = incomeTextColor,
                            fontSize = (20.sp * fontScale),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.average_per_day, "xxx.xxx"),
                            fontSize = (11.sp * fontScale),
                            color = textSecondary
                        )
                    }
                }
            }
        }
    }
}