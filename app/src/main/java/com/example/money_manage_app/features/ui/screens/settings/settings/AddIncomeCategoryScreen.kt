package com.example.money_manage_app.features.ui.screens.settings.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.FontSizeManager
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import com.example.money_manage_app.features.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeCategoryScreen(
    navController: NavHostController,
    categoryViewModel: CategoryViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val langPref = remember { LanguagePreference(context) }
    val fontSizeManager = remember { FontSizeManager(context) }

    val language by langPref.currentLanguage.collectAsState(initial = "Tiếng Việt")
    val fontScale by fontSizeManager.fontSizeFlow.collectAsState(initial = 1f)
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var name by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("AttachMoney") }

    val iconList = listOf(
        Icons.Default.AttachMoney to "AttachMoney",
        Icons.Default.Work to "Work",
        Icons.Default.Star to "Star",
        Icons.Default.AccountBalance to "AccountBalance",
        Icons.Default.Savings to "Savings",
        Icons.Default.TrendingUp to "TrendingUp",
        Icons.Default.MilitaryTech to "MilitaryTech",
        Icons.Default.Favorite to "Favorite",
        Icons.Default.AutoAwesome to "AutoAwesome",
        Icons.Default.Money to "Money",
        Icons.Default.Schedule to "Schedule",
        Icons.Default.MoreHoriz to "MoreHoriz"
    )

    val titleText = if (language == "English") "Add Income Category" else "Thêm danh mục thu nhập"
    val placeholderText = if (language == "English") "Enter category name" else "Vui lòng nhập tên danh mục"
    val iconTitleText = if (language == "English") "Select Icon" else "Chọn biểu tượng"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        titleText,
                        color = colors.onPrimary,
                        style = typography.titleLarge.copy(fontSize = 20.sp * fontScale)
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
                actions = {
                    IconButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                scope.launch {
                                    // ✅ SỬ DỤNG CategoryViewModel thay vì DataStore
                                    categoryViewModel.addCategory(
                                        name = name,
                                        iconName = selectedIconName,
                                        isExpense = false,
                                        nameNote = name // Lưu tên người dùng nhập vào nameNote
                                    )
                                    navController.popBackStack()
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Save",
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
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(colors.background)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text(placeholderText, fontSize = 14.sp * fontScale)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline
                )
            )

            Spacer(Modifier.height(24.dp))

            Text(
                iconTitleText,
                fontSize = 18.sp * fontScale,
                color = colors.onSurface
            )
            Spacer(Modifier.height(16.dp))

            // Grid icons
            Column(modifier = Modifier.fillMaxWidth()) {
                iconList.chunked(4).forEach { rowIcons ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowIcons.forEach { (icon, iconName) ->
                            IncomeIconItem(
                                icon = icon,
                                iconName = iconName,
                                isSelected = selectedIconName == iconName,
                                onClick = { selectedIconName = iconName },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(4 - rowIcons.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun IncomeIconItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = if (isSelected) colors.primary else Color.Transparent,
                    shape = CircleShape
                )
                .border(
                    width = if (isSelected) 0.dp else 1.dp,
                    color = colors.outline.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconName,
                tint = if (isSelected) colors.onPrimary else colors.onSurface,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}