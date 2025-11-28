package com.example.money_manage_app.features.ui.screens.settings.settings

import android.content.Context
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.FontSizeManager
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import com.example.money_manage_app.data.local.datastore.ThemePreference
import com.example.money_manage_app.features.navigation.Routes
import com.example.money_manage_app.features.viewmodel.CategoryViewModel
import com.example.money_manage_app.features.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySettingScreen(
    navController: NavHostController,
    categoryViewModel: CategoryViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // Theme & Font
    val themePref = remember { ThemePreference(context) }
    val fontManager = remember { FontSizeManager(context) }
    val languagePref = remember { LanguagePreference(context) }

    val isDark by themePref.isDarkMode.collectAsState(initial = false)
    val fontScale by fontManager.fontSizeFlow.collectAsState(initial = 1f)
    val currentLanguage by languagePref.currentLanguage.collectAsState(initial = "Tiếng Việt")
    val colors = MaterialTheme.colorScheme

    // ✅ Xác định ngôn ngữ hiện tại
    val isEnglish = currentLanguage == "English"

    // Tab
    var selectedTab by remember { mutableStateOf(0) }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }

    // UserId
    val currentUserId by userViewModel.currentUserId.collectAsState()
    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser(context)
    }

    // Categories
    val expenseCategories by categoryViewModel.expenseCategories.collectAsState()
    val incomeCategories by categoryViewModel.incomeCategories.collectAsState()
    val currentCategories = if (selectedTab == 0) expenseCategories else incomeCategories

    // ✅ Reload categories khi userId hoặc ngôn ngữ thay đổi
    LaunchedEffect(currentUserId, currentLanguage) {
        if (currentUserId.isNotEmpty()) {
            categoryViewModel.setUserId(currentUserId)
        }
    }

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
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colors.primary)
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
            // Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                TabButton(
                    text = stringResource(R.string.expense),
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0; draggedIndex = null; dragOffset = 0f },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = stringResource(R.string.income),
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1; draggedIndex = null; dragOffset = 0f },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            // ✅ Kiểm tra nếu danh sách rỗng
            if (currentCategories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = colors.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isEnglish) "No categories available" else "Không có danh mục",
                            color = colors.onSurface.copy(alpha = 0.6f),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isEnglish) "Add your first category below" else "Thêm danh mục đầu tiên bên dưới",
                            color = colors.onSurface.copy(alpha = 0.4f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
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
                        val targetIndex = if (isBeingDragged && dragOffset != 0f) {
                            val itemHeight = with(density) { 60.dp.toPx() }
                            val offset = (dragOffset / itemHeight).roundToInt()
                            (index + offset).coerceIn(0, currentCategories.size - 1)
                        } else index

                        // ✅ Tính toán tên hiển thị ĐÚNG theo ngôn ngữ hiện tại
                        val displayName = remember(category, isEnglish) {
                            when {
                                !category.nameNote.isNullOrBlank() -> category.nameNote!!
                                isEnglish -> category.nameEn ?: category.nameVi
                                else -> category.nameVi
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .graphicsLayer {
                                    shadowElevation = if (isBeingDragged) 12f else 0f
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
                            // Delete button
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

                            // Icon
                            Icon(
                                getIconFromName(category.iconName),
                                contentDescription = displayName,
                                tint = colors.onSurface,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // ✅ Hiển thị tên category với key để force recompose
                            key(category.id, isEnglish) {
                                Text(
                                    text = displayName,
                                    color = colors.onSurface,
                                    fontSize = (16.sp * fontScale),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Drag handle
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Kéo để sắp xếp",
                                tint = colors.outline,
                                modifier = Modifier
                                    .size(28.dp)
                                    .pointerInput(currentCategories.size) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = { draggedIndex = index; dragOffset = 0f },
                                            onDragEnd = {
                                                draggedIndex?.let { from ->
                                                    val itemHeight = with(density) { 60.dp.toPx() }
                                                    val offset = (dragOffset / itemHeight).roundToInt()
                                                    val to = (from + offset).coerceIn(0, currentCategories.size - 1)
                                                    if (from != to) moveCategory(from, to)
                                                }
                                                draggedIndex = null
                                                dragOffset = 0f
                                            },
                                            onDragCancel = { draggedIndex = null; dragOffset = 0f },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                dragOffset += dragAmount.y
                                            }
                                        )
                                    }
                            )
                        }
                    }
                }
            }

            // Add category button - Luôn hiển thị ở dưới cùng
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (selectedTab == 0) navController.navigate(Routes.AddExpenseCategory)
                        else navController.navigate(Routes.AddIncomeCategory)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_category), fontSize = (16.sp * fontScale))
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
                if (isSelected) Modifier.background(
                    if (isDark) Color(0xFF1E1E1E) else Color.White,
                    RoundedCornerShape(4.dp)
                ) else Modifier
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

fun getIconFromName(iconName: String): ImageVector {
    return when (iconName) {
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
        "CheckCircle" -> Icons.Default.CheckCircle
        else -> Icons.Default.Category
    }
}