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
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.FontSizeManager
import com.example.money_manage_app.data.local.datastore.ThemePreference
import com.example.money_manage_app.features.navigation.Routes
import com.example.money_manage_app.features.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySettingScreen(
    navController: NavHostController,
    categoryViewModel: CategoryViewModel
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val currentLanguage = configuration.locales.get(0)?.language ?: "en"
    val scope = rememberCoroutineScope()

    // Theme & Font managers
    val themePref = remember { ThemePreference(context) }
    val fontManager = remember { FontSizeManager(context) }
    val isDark by themePref.isDarkMode.collectAsState(initial = false)
    val fontScale by fontManager.fontSizeFlow.collectAsState(initial = 1f)
    val colors = MaterialTheme.colorScheme

    // Tab state
    var selectedTab by remember { mutableStateOf(0) }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }

    // Categories from ViewModel (Room Database)
    val expenseCategories by categoryViewModel.expenseCategories.collectAsState()
    val incomeCategories by categoryViewModel.incomeCategories.collectAsState()
    val currentCategories = if (selectedTab == 0) expenseCategories else incomeCategories

    // Load categories on first launch
    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
    }

    // Move category function
    fun moveCategory(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        categoryViewModel.reorderCategories(fromIndex, toIndex, selectedTab == 0)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.setting_category),
                        color = Color.Black,
                        style = typography.titleMedium.copy(fontSize = 20.sp)
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
            // Custom Tab Switcher (giống AddTransactionScreen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 16.dp)
            ) {
                // Background xám/tối tùy theme
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(
                            if (isDark) Color(0xFF2C2C2C) else Color(0xFFE8E8E8),
                            RoundedCornerShape(6.dp)
                        )
                ) {}

                // Các nút tab
                Row(modifier = Modifier.fillMaxWidth()) {
                    TabButton(
                        text = stringResource(R.string.expense),
                        isSelected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            draggedIndex = null
                            dragOffset = 0f
                        },
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = stringResource(R.string.income),
                        isSelected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            draggedIndex = null
                            dragOffset = 0f
                        },
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(
                    items = currentCategories,
                    key = { _, item -> item.id }
                ) { index, category ->
                    val isBeingDragged = draggedIndex == index

                    // Tính vị trí đích
                    val targetIndex = if (isBeingDragged && dragOffset != 0f) {
                        val itemHeight = 60f
                        val offset = (dragOffset / itemHeight).toInt()
                        (index + offset).coerceIn(0, currentCategories.size - 1)
                    } else {
                        index
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .graphicsLayer {
                                shadowElevation = if (isBeingDragged) 12.dp.toPx() else 0f
                                alpha = if (isBeingDragged) 0.9f else 1f
                                translationY = if (isBeingDragged) dragOffset else 0f
                            }
                            .background(
                                when {
                                    isBeingDragged -> colors.surfaceVariant.copy(alpha = 0.8f)
                                    draggedIndex != null && index == targetIndex && index != draggedIndex ->
                                        colors.primary.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nút xóa
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFEF5350), CircleShape)
                                .clickable {
                                    scope.launch {
                                        categoryViewModel.deleteCategory(category)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Icon category
                        Icon(
                            getIconFromName(category.iconName),
                            contentDescription = category.nameNote
                                ?: category.nameEn
                                ?: category.nameVi,
                            tint = colors.onSurface,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Tên category (hiển thị theo ngôn ngữ)
                        Text(
                            text = category.nameNote ?: if (currentLanguage == "vi")
                                category.nameVi else category.nameEn,
                            color = colors.onSurface,
                            fontSize = (16.sp * fontScale),
                            modifier = Modifier.weight(1f)
                        )

                        // Nút kéo (3 gạch menu)
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Kéo để sắp xếp",
                            tint = colors.outline,
                            modifier = Modifier
                                .size(28.dp)
                                .pointerInput(currentCategories.size) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            draggedIndex = index
                                            dragOffset = 0f
                                        },
                                        onDragEnd = {
                                            val from = draggedIndex
                                            if (from != null) {
                                                val itemHeight = 60f
                                                val offset = (dragOffset / itemHeight).toInt()
                                                val to = (from + offset).coerceIn(
                                                    0,
                                                    currentCategories.size - 1
                                                )
                                                if (from != to) {
                                                    moveCategory(from, to)
                                                }
                                            }
                                            draggedIndex = null
                                            dragOffset = 0f
                                        },
                                        onDragCancel = {
                                            draggedIndex = null
                                            dragOffset = 0f
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffset += dragAmount.y
                                        }
                                    )
                                }
                        )
                    }
                }

                // Nút thêm danh mục
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (selectedTab == 0)
                                navController.navigate(Routes.AddExpenseCategory)
                            else
                                navController.navigate(Routes.AddIncomeCategory)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.add_category),
                            fontSize = (16.sp * fontScale)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .padding(4.dp)
            .then(
                if (isSelected) {
                    Modifier.background(
                        if (isDark) Color(0xFF1E1E1E) else Color.White,
                        RoundedCornerShape(4.dp)
                    )
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
            color = if (isDark) Color.White else Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

// Helper function để map icon name -> ImageVector
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

        // Thu nhập
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

        // Dự phòng
        else -> Icons.Default.Category
    }
}