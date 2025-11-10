package com.example.money_manage_app.features.ui.screens.settings.settings

import com.example.money_manage_app.features.navigation.Routes
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.data.local.datastore.*
import kotlinx.coroutines.launch

data class CategoryItem(val icon: ImageVector, val name: String, val iconName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySettingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val categoryPreference = remember { CategoryPreference(context) }

    // 🔹 Thêm 2 dòng này
    val themePref = remember { ThemePreference(context) }
    val fontManager = remember { FontSizeManager(context) }

    val isDark by themePref.isDarkMode.collectAsState(initial = false)
    val fontScale by fontManager.fontSizeFlow.collectAsState(initial = 1f)
    val colors = MaterialTheme.colorScheme

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
        categoryPreference.categoriesFlow.collect { saved ->
            expenseCategories = saved["expense"]?.map { data ->
                val icon = getIconFromName(data.iconName)
                CategoryItem(icon, data.name, data.iconName)
            } ?: defaultExpenseCategories

            incomeCategories = saved["income"]?.map { data ->
                val icon = getIconFromName(data.iconName)
                CategoryItem(icon, data.name, data.iconName)
            } ?: defaultIncomeCategories
        }
    }

    fun saveCategories() {
        scope.launch {
            val expenseData = expenseCategories.map { CategoryData(it.iconName, it.name) }
            val incomeData = incomeCategories.map { CategoryData(it.iconName, it.name) }
            val data = mapOf("expense" to expenseData, "income" to incomeData)
            categoryPreference.save(data)
        }
    }

    fun deleteCategory(index: Int) {
        if (selectedTab == 0)
            expenseCategories = expenseCategories.toMutableList().apply { removeAt(index) }
        else
            incomeCategories = incomeCategories.toMutableList().apply { removeAt(index) }

        saveCategories()
    }

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
                        color = colors.onPrimary,
                        fontSize = (20.sp * fontScale)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.primary
                )
            )
        },
        containerColor = colors.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(colors.background)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = colors.surfaceVariant,
                contentColor = colors.onSurface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Chi tiêu", fontSize = (16.sp * fontScale)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Thu nhập", fontSize = (16.sp * fontScale)) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(currentCategories, key = { index, item -> "${item.name}-$index" }) { index, category ->
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
                                if (targetIndex == index) colors.surfaceVariant else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nút xóa
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFEF5350), CircleShape)
                                .clickable { deleteCategory(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-", color = colors.onError, fontSize = (18.sp * fontScale))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Icon + Tên
                        Icon(category.icon, contentDescription = category.name, tint = colors.onSurface)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            category.name,
                            color = colors.onSurface,
                            fontSize = (16.sp * fontScale)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Nút drag
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Reorder",
                            tint = colors.outline,
                            modifier = Modifier.pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { draggedIndex = index },
                                    onDragEnd = {
                                        if (draggedIndex != null && targetIndex != null && draggedIndex != targetIndex)
                                            moveCategory(draggedIndex!!, targetIndex!!)
                                        draggedIndex = null
                                        targetIndex = null
                                    },
                                    onDragCancel = {
                                        draggedIndex = null
                                        targetIndex = null
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val itemHeight = 60.dp.toPx()
                                        val offset = (dragAmount.y / itemHeight).toInt()
                                        val newTarget =
                                            (index + offset).coerceIn(0, currentCategories.size - 1)
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
                        onClick = {
                            if (selectedTab == 0)
                                navController.navigate(Routes.AddExpenseCategory)
                            else
                                navController.navigate(Routes.AddIncomeCategory)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Thêm danh mục", fontSize = (16.sp * fontScale))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// Helper function
fun getIconFromName(iconName: String): ImageVector {
    return when (iconName) {
        // Chi tiêu
        "Restaurant" -> Icons.Default.Restaurant
        "LocalBar" -> Icons.Default.LocalBar
        "Flight" -> Icons.Default.Flight
        "Movie" -> Icons.Default.Movie
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "LocalGasStation" -> Icons.Default.LocalGasStation
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "SportsSoccer" -> Icons.Default.SportsSoccer
        "EmojiFoodBeverage" -> Icons.Default.EmojiFoodBeverage

        // Thu nhập
        "AttachMoney" -> Icons.Default.AttachMoney
        "Work" -> Icons.Default.Work
        "Star" -> Icons.Default.Star
        "AccountBalance" -> Icons.Default.AccountBalance
        "Savings" -> Icons.Default.Savings
        "TrendingUp" -> Icons.Default.TrendingUp
        "MilitaryTech" -> Icons.Default.MilitaryTech
        "Favorite" -> Icons.Default.Favorite
        "AutoAwesome" -> Icons.Default.AutoAwesome

        // Dự phòng
        else -> Icons.Default.Category
    }
}

