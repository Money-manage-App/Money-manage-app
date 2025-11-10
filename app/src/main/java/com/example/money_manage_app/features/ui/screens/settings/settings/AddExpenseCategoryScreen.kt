package com.example.money_manage_app.features.ui.screens.settings.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.data.local.datastore.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseCategoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val categoryPref = remember { CategoryPreference(context) }
    val langPref = remember { LanguagePreference(context) }
    val fontSizeManager = remember { FontSizeManager(context) }

    val language by langPref.currentLanguage.collectAsState(initial = "Tiếng Việt")
    val fontScale by fontSizeManager.fontSizeFlow.collectAsState(initial = 1f)
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var name by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("ShoppingCart") }

    val iconList = listOf(
        Icons.Default.Restaurant to "Restaurant",
        Icons.Default.LocalBar to "LocalBar",
        Icons.Default.Flight to "Flight",
        Icons.Default.Movie to "Movie",
        Icons.Default.ShoppingCart to "ShoppingCart",
        Icons.Default.LocalGasStation to "LocalGasStation",
        Icons.Default.FitnessCenter to "FitnessCenter",
        Icons.Default.SportsSoccer to "SportsSoccer",
        Icons.Default.EmojiFoodBeverage to "EmojiFoodBeverage"
    )

    val titleText = if (language == "English") "Add Expense Category" else "Thêm danh mục chi tiêu"
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.onPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (name.isNotBlank()) {
                            scope.launch {
                                categoryPref.saveNewCategory("expense", CategoryData(selectedIconName, name))
                                navController.popBackStack()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = colors.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colors.primary)
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
                placeholder = { Text(placeholderText, fontSize = 14.sp * fontScale) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Text(iconTitleText, fontSize = 18.sp * fontScale, color = colors.onSurface)
            Spacer(Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                iconList.forEach { (icon, iconName) ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (selectedIconName == iconName) colors.primary else colors.surfaceVariant,
                                CircleShape
                            )
                            .clickable { selectedIconName = iconName },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = iconName, tint = colors.onSurface)
                    }
                }
            }
        }
    }
}
