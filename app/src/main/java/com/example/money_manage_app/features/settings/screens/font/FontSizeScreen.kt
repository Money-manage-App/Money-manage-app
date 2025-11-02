package com.example.money_manage_app.features.settings.screens.font

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.features.settings.data.FontSizeManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSizeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val fontSizeManager = remember { FontSizeManager(context) }
    val scope = rememberCoroutineScope()
    val fontScale by fontSizeManager.fontSizeFlow.collectAsState(initial = 1f)
    var sliderValue by rememberSaveable { mutableStateOf(fontScale) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cỡ chữ", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Điều chỉnh cỡ chữ", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))

            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    scope.launch { fontSizeManager.setFontSize(it) }
                },
                valueRange = 0.8f..1.5f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Văn bản mẫu hiển thị",
                fontSize = (16.sp * sliderValue),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
