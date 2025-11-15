package com.example.money_manage_app.features.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.ThemePreference
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val themePreference = remember { ThemePreference(context) }
    val languagePreference = remember { LanguagePreference(context) }

    val isDarkMode by themePreference.isDarkMode.collectAsState(initial = false)
    val currentLanguage by languagePreference.currentLanguage.collectAsState(initial = "Tiếng Việt")

    // Định nghĩa màu sắc theo theme
    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val topBarColor = Color(0xFFFFEB3B) // Luôn giữ màu vàng
    val cardBackgroundColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.Black
    val textPrimaryColor = if (isDarkMode) Color.White else Color.Black
    val textOnCardColor = Color.White
    val borderColor = if (isDarkMode) Color(0xFF404040) else Color.Gray
    val expenseBoxColor = Color(0xFFFFEB3B) // Luôn giữ màu vàng
    val incomeBoxColor = Color(0xFFF5F5F5) // Luôn giữ màu xám nhạt

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- Thanh trên ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(topBarColor)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = stringResource(R.string.home_title),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black, // Luôn giữ màu đen
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 8.dp)
            )
        }

        // --- Khung thông tin tổng quan ---
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(160.dp)
                .padding(top = 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(cardBackgroundColor)
                .padding(horizontal = 13.dp, vertical = 16.dp)
                .fillMaxWidth(0.9f)
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.total_balance))
                    withStyle(style = SpanStyle(color = Color.Yellow)) { append("0 đ") }
                },
                color = textOnCardColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.total_debt))
                    withStyle(style = SpanStyle(color = Color.Red)) { append("0 đ") }
                },
                color = textOnCardColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // --- Nội dung chính ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 170.dp, start = 12.dp, end = 12.dp)
        ) {
            // --- Chọn tháng ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .background(if (isDarkMode) Color(0xFF2C2C2C) else Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.month_10),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textPrimaryColor
                )
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Chọn tháng",
                    tint = textPrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Hai ô Chi tiêu / Thu nhập ---
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
                        color = Color.Black // Text màu đen trên nền vàng
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "0 đ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black // Text màu đen trên nền vàng
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
                        color = Color.Black // Text màu đen trên nền xám nhạt
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "0 đ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black // Text màu đen trên nền xám nhạt
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            // --- Biểu đồ tròn ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(
                    data = listOf(70f, 30f),
                    colors = listOf(Color(0xFF607D8B), Color(0xFFFF9800)),
                    backgroundColor = backgroundColor
                )
            }
        }
    }
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

            // Lỗ tròn ở giữa (dùng màu nền theo theme)
            drawCircle(
                color = backgroundColor,
                radius = size.minDimension * holeRatio / 2
            )
        }

        // Hiển thị phần trăm bên ngoài biểu đồ
        val radius = 90f
        var startAngle = -90f
        for (i in data.indices) {
            val midAngle = startAngle + angles[i] / 2
            val x = (radius * cos(Math.toRadians(midAngle.toDouble()))).toFloat()
            val y = (radius * sin(Math.toRadians(midAngle.toDouble()))).toFloat()

            Text(
                text = "${(data[i] / total * 100).toInt()}%",
                color = colors.getOrElse(i) { Color.Gray },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x.dp, y.dp)
            )

            startAngle += angles[i]
        }
    }
}