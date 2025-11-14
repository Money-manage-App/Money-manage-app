package com.example.money_manage_app.features.ui.screens.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class Category(
    val name: String,
    val icon: ImageVector,
    val isExpense: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Chi tiêu, 1 = Thu nhập
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var amount by remember { mutableStateOf("0") }
    var note by remember { mutableStateOf("") }

    // Danh sách categories cho Chi tiêu
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

    // Danh sách categories cho Thu nhập
    val incomeCategories = listOf(
        Category("Lương", Icons.Default.AccountBalance, false),
        Category("Khoản tiết kiệm", Icons.Default.Savings, false),
        Category("Tiền lãi", Icons.Default.TrendingUp, false),
        Category("Khác", Icons.Default.MoreHoriz, false)
    )

    val categories = if (selectedTab == 0) expenseCategories else incomeCategories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header vàng
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Thêm",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Tab Chi tiêu / Thu nhập - Nền xám chung
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp)
        ) {
            // Nền xám cho cả 2 tab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color(0xFFE8E8E8), RoundedCornerShape(6.dp))
            ) {}

            // Các tab button đè lên nền xám
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TabButton(
                    text = "Chi tiêu",
                    isSelected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        selectedCategory = null
                    },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Thu nhập",
                    isSelected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        selectedCategory = null
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Grid categories - 4 cột
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
                            isSelected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Thêm spacer nếu hàng không đủ 4 items
                    repeat(4 - rowCategories.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Amount display với viền trên
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            horizontalAlignment = Alignment.End
        ) {
            Divider(thickness = 1.dp, color = Color(0xFFE0E0E0))

            Text(
                text = amount,
                fontSize = 56.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.padding(end = 24.dp, top = 16.dp)
            )

            Text(
                text = if (note.isEmpty()) "Ghi chú :  Nhập ghi chú..." else "Ghi chú :  $note",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(end = 24.dp, top = 4.dp, bottom = 16.dp)
            )
        }

        Divider(thickness = 1.dp, color = Color(0xFFE0E0E0))

        // Number pad
        NumberPad(
            onNumberClick = { number ->
                if (amount == "0") {
                    amount = number
                } else {
                    amount += number
                }
            },
            onDeleteClick = {
                if (amount.isNotEmpty()) {
                    amount = if (amount.length == 1) "0" else amount.dropLast(1)
                }
            },
            onClearClick = {
                amount = "0"
            },
            onConfirmClick = {
                // TODO: Lưu giao dịch
                navController.popBackStack()
            }
        )
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

@Composable
fun NumberPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val currentDate = remember {
        val calendar = java.util.Calendar.getInstance()
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        Triple(day, month, calendar.get(java.util.Calendar.YEAR))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Row 1: 7, 8, 9, date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("7", onClick = { onNumberClick("7") }, modifier = Modifier.weight(1f))
            NumberButton("8", onClick = { onNumberClick("8") }, modifier = Modifier.weight(1f))
            NumberButton("9", onClick = { onNumberClick("9") }, modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(Color(0xFFFFF9C4), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("${currentDate.first} Thg ${currentDate.second}", fontSize = 8.sp, color = Color(0xFF888888))
                    Text("${currentDate.first}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("${currentDate.third}", fontSize = 8.sp, color = Color(0xFF888888))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: 4, 5, 6, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("4", onClick = { onNumberClick("4") }, modifier = Modifier.weight(1f))
            NumberButton("5", onClick = { onNumberClick("5") }, modifier = Modifier.weight(1f))
            NumberButton("6", onClick = { onNumberClick("6") }, modifier = Modifier.weight(1f))
            OperatorButton("+", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 3: 1, 2, 3, -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("1", onClick = { onNumberClick("1") }, modifier = Modifier.weight(1f))
            NumberButton("2", onClick = { onNumberClick("2") }, modifier = Modifier.weight(1f))
            NumberButton("3", onClick = { onNumberClick("3") }, modifier = Modifier.weight(1f))
            OperatorButton("-", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 4: ,, 0, delete, confirm
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton(",", onClick = { onNumberClick(".") }, modifier = Modifier.weight(1f))
            NumberButton("0", onClick = { onNumberClick("0") }, modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(4.dp))
                    .clickable(onClick = onDeleteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Delete",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(Color(0xFFDDDDDD), RoundedCornerShape(4.dp))
                    .clickable(onClick = onConfirmClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun NumberButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(Color(0xFFFAFAFA), RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    }
}

@Composable
fun OperatorButton(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(Color(0xFFFAFAFA), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    }
}