package com.example.money_manage_app.features.ui.screens.settings.font

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.*
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSizeScreen(navController: NavHostController) {

    val context = LocalContext.current
    val fontSizeManager = remember { FontSizeManager(context) }
    val scope = rememberCoroutineScope()

    val fontScale by fontSizeManager.fontSizeFlow.collectAsState(initial = 1f)

    val fontOptions = listOf(
        0.9f to "A",
        1.0f to "A",
        1.2f to "A",
        1.4f to "A"
    )

    var sliderValue by rememberSaveable { mutableStateOf(fontScale) }

    LaunchedEffect(fontScale) {
        sliderValue = fontScale
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.font_size),
                    color = Color.Black,
                    style = typography.titleMedium.copy(fontSize = 20.sp)
                ) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = stringResource(R.string.adjust_font_size),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            /** ---------------- SLIDER MỎNG ---------------- **/
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = {
                    val nearest = fontOptions.minByOrNull { (scale, _) ->
                        abs(scale - sliderValue)
                    }?.first ?: sliderValue

                    scope.launch { fontSizeManager.setFontSize(nearest) }
                },
                valueRange = 0.9f..1.4f,
                steps = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)   // 👉 thanh mỏng lại
                    .padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            /** ---------- NÚT A căn thẳng 4 mốc slider ---------- **/
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                fontOptions.forEach { (scale, label) ->

                    val previewSize = when (scale) {
                        0.9f -> 14.sp
                        1.0f -> 18.sp
                        1.2f -> 22.sp
                        else -> 26.sp
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)       // 👉 chia đều giống slider steps
                            .wrapContentSize(Alignment.Center)
                            .clickable {
                                scope.launch { fontSizeManager.setFontSize(scale) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            Text(
                                text = label,
                                fontSize = previewSize,
                                color = if (fontScale == scale)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )

                            if (fontScale == scale) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .height(3.dp)
                                        .width(24.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
