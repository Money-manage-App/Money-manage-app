package com.example.money_manage_app.features.ui.screens.settings.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.CurrencyPreference
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySettingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val pref = remember { CurrencyPreference(context) }
    val scope = rememberCoroutineScope()

    val currentCurrency by pref.currentCurrency.collectAsState(initial = "VND")
    val colors = MaterialTheme.colorScheme

    val currencyList = listOf(
        "VND" to "₫",
        "USD" to "$",
        "EUR" to "€",
        "JPY" to "¥",
        "GBP" to "£",
        "KRW" to "₩"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.currency), color = colors.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = colors.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            currencyList.forEach { (code, symbol) ->
                val isSelected = (currentCurrency == code)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 6.dp)
                        .then(
                            if (isSelected)
                                Modifier.border(2.dp, colors.primary, RoundedCornerShape(16.dp))
                            else Modifier
                        )
                        .clickable { scope.launch { pref.setCurrency(code) } },
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$code ($symbol)", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.weight(1f))
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = colors.primary)
                        }
                    }
                }
            }
        }
    }
}
