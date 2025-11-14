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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Thanh vàng ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFFFFEB3B))
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = "Trang chủ",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 8.dp)
            )
        }

        // --- Khung đen ---
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(160.dp)
                .padding(top = 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
                .padding(horizontal = 13.dp, vertical = 16.dp)
                .fillMaxWidth(0.9f)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Tổng số dư: ")
                    withStyle(style = SpanStyle(color = Color.Yellow)) { append("0 đ") }
                },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = buildAnnotatedString {
                    append("Tổng số nợ: ")
                    withStyle(style = SpanStyle(color = Color.Red)) { append("0 đ") }
                },
                color = Color.White,
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
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tháng 10",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Chọn tháng")
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
                        .background(Color(0xFFFFEB3B))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Chi tiêu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("0 đ", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Thu nhập", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("0 đ", fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
                    Text("Thu nhập", color = Color(0xFF607D8B), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(Color(0xFFFF9800)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chi tiêu", color = Color(0xFFFF9800), fontSize = 14.sp)
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
                    colors = listOf(Color(0xFF607D8B), Color(0xFFFF9800))
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
    holeRatio: Float = 0.6f
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

            // Lỗ tròn ở giữa
            drawCircle(
                color = Color.White,
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
