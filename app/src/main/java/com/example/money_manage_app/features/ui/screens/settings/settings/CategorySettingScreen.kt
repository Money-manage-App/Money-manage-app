package com.example.money_manage_app.features.ui.screens.settings.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.data.local.datastore.CategoryPreference
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class CategoryData(
    val iconName: String,
    val name: String
)

data class CategoryItem(val icon: ImageVector, val name: String, val iconName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySettingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val categoryPreference = remember { CategoryPreference(context) }

    var selectedTab by remember { mutableStateOf(0) }
    var expenseCategories by remember { mutableStateOf<List<CategoryItem>>(emptyList()) }
    var incomeCategories by remember { mutableStateOf<List<CategoryItem>>(emptyList()) }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var targetIndex by remember { mutableStateOf<Int?>(null) }

    // Danh mục mặc định
    val defaultExpenseCategories = listOf(
        CategoryItem(Icons.Default.ShoppingCart, "Mua sắm", "ShoppingCart"),
        CategoryItem(Icons.Default.SportsEsports, "Giải trí", "SportsEsports"),
        CategoryItem(Icons.Default.Checkroom, "Quần áo", "Checkroom"),
        CategoryItem(Icons.Default.Pets, "Thú cưng", "Pets"),
        CategoryItem(Icons.Default.Restaurant, "Đồ ăn", "Restaurant"),
        CategoryItem(Icons.Default.SportsSoccer, "Thể thao", "SportsSoccer"),
        CategoryItem(Icons.Default.Favorite, "Sức khỏe", "Favorite"),
        CategoryItem(Icons.Default.Build, "Sửa chữa", "Build"),
        CategoryItem(Icons.Default.CardGiftcard, "Biếu tặng", "CardGiftcard")
    )

    val defaultIncomeCategories = listOf(
        CategoryItem(Icons.Default.Money, "Lương", "Money"),
        CategoryItem(Icons.Default.Savings, "Khoản đầu tư", "Savings"),
        CategoryItem(Icons.Default.Schedule, "Bán thời gian", "Schedule"),
        CategoryItem(Icons.Default.MoreHoriz, "Khác", "MoreHoriz")
    )

    // Load dữ liệu từ DataStore
    LaunchedEffect(Unit) {
        categoryPreference.categoriesFlow.collect { savedJson ->
            if (savedJson.isNotEmpty()) {
                try {
                    val saved = Json.decodeFromString<Map<String, List<CategoryData>>>(savedJson.first())

                    expenseCategories = saved["expense"]?.map { data ->
                        val icon = getIconFromName(data.iconName)
                        CategoryItem(icon, data.name, data.iconName)
                    } ?: defaultExpenseCategories

                    incomeCategories = saved["income"]?.map { data ->
                        val icon = getIconFromName(data.iconName)
                        CategoryItem(icon, data.name, data.iconName)
                    } ?: defaultIncomeCategories
                } catch (e: Exception) {
                    expenseCategories = defaultExpenseCategories
                    incomeCategories = defaultIncomeCategories
                }
            } else {
                expenseCategories = defaultExpenseCategories
                incomeCategories = defaultIncomeCategories
            }
        }
    }

    // Hàm lưu vào DataStore
    fun saveCategories() {
        scope.launch {
            val expenseData = expenseCategories.map { CategoryData(it.iconName, it.name) }
            val incomeData = incomeCategories.map { CategoryData(it.iconName, it.name) }
            val data = mapOf("expense" to expenseData, "income" to incomeData)
            categoryPreference.save(listOf(Json.encodeToString(data)))
        }
    }

    // Hàm xóa danh mục
    fun deleteCategory(index: Int) {
        if (selectedTab == 0) {
            expenseCategories = expenseCategories.toMutableList().apply { removeAt(index) }
        } else {
            incomeCategories = incomeCategories.toMutableList().apply { removeAt(index) }
        }
        saveCategories()
    }

    // Hàm di chuyển danh mục
    fun moveCategory(fromIndex: Int, toIndex: Int) {
        if (selectedTab == 0) {
            val list = expenseCategories.toMutableList()
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            expenseCategories = list
        } else {
            val list = incomeCategories.toMutableList()
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            incomeCategories = list
        }
        saveCategories()
    }

    val currentCategories = if (selectedTab == 0) expenseCategories else incomeCategories

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Cài đặt danh mục",
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFEE912)
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Chi tiêu") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Thu nhập") }
                )
            }

            // Danh sách category với drag and drop
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(
                    items = currentCategories,
                    key = { index, item -> "${item.name}-$index" }
                ) { index, category ->
                    val isBeingDragged = draggedIndex == index
                    val elevation = if (isBeingDragged) 8.dp else 0.dp

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .graphicsLayer {
                                shadowElevation = elevation.toPx()
                                alpha = if (isBeingDragged) 0.8f else 1f
                            }
                            .background(
                                if (targetIndex == index) Color(0xFFE0E0E0) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nút xóa
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFD32F2F), CircleShape)
                                .clickable {
                                    deleteCategory(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-", color = Color.White, fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Icon + Tên
                        Icon(category.icon, contentDescription = category.name, tint = Color.Black)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(category.name, color = Color.Black, fontSize = 16.sp)

                        Spacer(modifier = Modifier.weight(1f))

                        // Nút sắp xếp (drag handle)
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Reorder",
                            tint = Color.Gray,
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            draggedIndex = index
                                        },
                                        onDragEnd = {
                                            if (draggedIndex != null && targetIndex != null &&
                                                draggedIndex != targetIndex) {
                                                moveCategory(draggedIndex!!, targetIndex!!)
                                            }
                                            draggedIndex = null
                                            targetIndex = null
                                        },
                                        onDragCancel = {
                                            draggedIndex = null
                                            targetIndex = null
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            // Tính toán vị trí mục tiêu dựa trên drag amount
                                            val itemHeight = 60.dp.toPx()
                                            val offset = (dragAmount.y / itemHeight).toInt()
                                            val newTarget = (index + offset).coerceIn(0, currentCategories.size - 1)
                                            targetIndex = newTarget
                                        }
                                    )
                                }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { /* TODO: thêm danh mục */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEE912),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Thêm danh mục")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// Helper function để map tên icon thành ImageVector
fun getIconFromName(iconName: String): ImageVector {
    return when (iconName) {
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "SportsEsports" -> Icons.Default.SportsEsports
        "Checkroom" -> Icons.Default.Checkroom
        "Pets" -> Icons.Default.Pets
        "Restaurant" -> Icons.Default.Restaurant
        "SportsSoccer" -> Icons.Default.SportsSoccer
        "Favorite" -> Icons.Default.Favorite
        "Build" -> Icons.Default.Build
        "CardGiftcard" -> Icons.Default.CardGiftcard
        "Money" -> Icons.Default.Money
        "Savings" -> Icons.Default.Savings
        "Schedule" -> Icons.Default.Schedule
        "MoreHoriz" -> Icons.Default.MoreHoriz
        else -> Icons.Default.Category
    }
}