package com.example.money_manage_app.features.ui.screens.history

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.money_manage_app.R
import com.example.money_manage_app.data.local.datastore.FontSizeManager
import com.example.money_manage_app.data.local.datastore.ThemePreference
import com.example.money_manage_app.data.local.datastore.LanguagePreference
import java.text.SimpleDateFormat
import java.util.*


data class Transaction(
    val id: Int,
    val title: String,
    val category: String,
    val amount: Double,
    val isIncome: Boolean,
    val time: String,
    val icon: ImageVector,
    val categoryColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val fontSizeManager = remember { FontSizeManager(context) }
    val fontScale by fontSizeManager.fontSizeFlow.collectAsState(initial = 1f)

    val themePreference = remember { ThemePreference(context) }
    val isDarkMode by themePreference.isDarkMode.collectAsState(initial = false)

    val languagePreference = remember { LanguagePreference(context) }
    val currentLanguage by languagePreference.currentLanguage.collectAsState(initial = "Tiếng Việt")

    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val headerColor = Color(0xFFFFD600)
    val headerTextColor = Color.Black
    val textColor = if (isDarkMode) Color.White else Color.Black
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF3E3E3E) else Color.LightGray
    val buttonColor = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFF4A4A4A)
    val buttonTextColor = Color(0xFFFFD600)

    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())

    val transactions = remember { mutableStateListOf<Transaction>() }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header vàng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = headerColor,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 40.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.history_title),
                    fontSize = (20.sp * fontScale),
                    fontWeight = FontWeight.SemiBold,
                    color = headerTextColor
                )
            }

            // Date Picker Field
            Surface(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, borderColor),
                color = cardColor,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .offset(y = (-24).dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateFormatter.format(Date(selectedDate)),
                        fontSize = (16.sp * fontScale),
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = stringResource(id = R.string.select_date),
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(-12.dp))

            // Title
            Text(
                text = stringResource(id = R.string.transaction_list),
                fontWeight = FontWeight.Bold,
                fontSize = (18.sp * fontScale),
                color = textColor,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp)
            )

            // Empty state
            if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_empty_state),
                        contentDescription = "Empty",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(id = R.string.no_transactions),
                        fontSize = (18.sp * fontScale),
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("add") },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "+ ",
                            fontSize = (18.sp * fontScale),
                            fontWeight = FontWeight.Bold,
                            color = buttonTextColor
                        )
                        Text(
                            text = stringResource(id = R.string.add_transaction),
                            fontSize = (15.sp * fontScale),
                            color = Color.White
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            fontScale = fontScale,
                            isDarkMode = isDarkMode
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDate = it }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK", color = Color(0xFF3C2E7E))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(id = R.string.cancel), color = Color.Gray)
                    }
                },
                colors = DatePickerDefaults.colors(containerColor = Color.White)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = Color(0xFF3C2E7E),
                        todayContentColor = Color(0xFF3C2E7E),
                        todayDateBorderColor = Color(0xFF3C2E7E)
                    )
                )
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, fontScale: Float = 1f, isDarkMode: Boolean = false) {
    val textColor = if (isDarkMode) Color.White else Color.Black
    val iconBgColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = transaction.icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                if (transaction.title.isNotEmpty()) {
                    Text(
                        text = transaction.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (15.sp * fontScale),
                        color = textColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = transaction.categoryColor,
                    modifier = Modifier.padding(top = if (transaction.title.isNotEmpty()) 4.dp else 0.dp)
                ) {
                    Text(
                        text = transaction.category,
                        color = if (transaction.isIncome) Color.Black else Color.White,
                        fontSize = (12.sp * fontScale),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = transaction.time,
                color = Color.Gray,
                fontSize = (12.sp * fontScale)
            )

            Text(
                text = (if (transaction.isIncome) "+" else "-") +
                        String.format("%,.0f", transaction.amount) + " đ",
                color = if (transaction.isIncome) Color(0xFF4CAF50) else Color(0xFFE53935),
                fontSize = (15.sp * fontScale),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
