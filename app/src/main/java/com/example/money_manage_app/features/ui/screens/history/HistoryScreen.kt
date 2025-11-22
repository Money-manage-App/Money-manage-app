package com.example.money_manage_app.features.ui.screens.history

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.draw.clip



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

// Singleton để lưu trữ danh sách giao dịch
object TransactionRepository {
    val transactions = mutableStateListOf(
        Transaction(
            id = 1,
            title = "Bún bò",
            category = "Ăn uống",
            amount = 30000.0,
            isIncome = false,
            time = "15:30",
            icon = Icons.Default.CalendarToday,
            categoryColor = Color(0xFF4CAF50)
        ),
        Transaction(
            id = 2,
            title = "Quần áo",
            category = "Mua sắm",
            amount = 500000.0,
            isIncome = false,
            time = "00:00",
            icon = Icons.Default.ShoppingCart,
            categoryColor = Color(0xFF3F51B5)
        ),
        Transaction(
            id = 3,
            title = "Trà sữa",
            category = "Ăn uống",
            amount = 40000.0,
            isIncome = false,
            time = "15:30",
            icon = Icons.Default.CalendarToday,
            categoryColor = Color(0xFF4CAF50)
        ),
        Transaction(
            id = 4,
            title = "Lương",
            category = "Lương",
            amount = 10000000.0,
            isIncome = true,
            time = "11:30",
            icon = Icons.Default.CalendarToday,
            categoryColor = Color(0xFFFFC107)
        )
    )

    fun deleteTransaction(id: Int) {
        transactions.removeAll { it.id == id }
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        val index = transactions.indexOfFirst { it.id == updatedTransaction.id }
        if (index != -1) {
            transactions[index] = updatedTransaction
        }
    }
}

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
    val headerColor = Color(0xFFFEE912)
    val headerTextColor = Color.Black
    val textColor = if (isDarkMode) Color.White else Color.Black
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val borderColor = if (isDarkMode) Color(0xFF3E3E3E) else Color.LightGray
    val buttonColor = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFF4A4A4A)
    val buttonTextColor = Color(0xFFFEE912)

    val iconColor = if (isDarkMode) Color.White else Color.Black
    val datecolor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())

    val transactions = TransactionRepository.transactions

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
                    text = stringResource(R.string.history_title),
                    fontSize = (20.sp * fontScale),
                    fontWeight = FontWeight.SemiBold,
                    color = headerTextColor
                )
            }

            // Date Picker
            Surface(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, borderColor),
                color = datecolor,
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
                        color = iconColor,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = stringResource(R.string.select_date),
                            tint = iconColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(-12.dp))

            Text(
                text = stringResource(R.string.transaction_list),
                fontWeight = FontWeight.Bold,
                fontSize = (18.sp * fontScale),
                color = textColor,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp)
            )

            // If empty
            if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_empty_state),
                        contentDescription = "Empty",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.no_transactions),
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
                            isDarkMode = isDarkMode,
                            onClick = {
                                navController.navigate("detail/${transaction.id}")
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

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

// ITEM CLICKABLE
@Composable
fun TransactionItem(
    transaction: Transaction,
    fontScale: Float = 1f,
    isDarkMode: Boolean = false,
    onClick: () -> Unit
) {
    val textColor = if (isDarkMode) Color.White else Color.Black
    val iconBgColor = if (isDarkMode) Color(0xFF2C2C2C) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavHostController,
    transactionId: Int
) {
    val context = LocalContext.current
    val transaction = TransactionRepository.transactions.firstOrNull { it.id == transactionId }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (transaction == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Không tìm thấy giao dịch!", color = Color.Red)
        }
        return
    }

    // Dialog xác nhận xóa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa giao dịch này?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        TransactionRepository.deleteTransaction(transactionId)
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Dialog sửa giao dịch
    if (showEditDialog) {
        EditTransactionDialog(
            transaction = transaction,
            onDismiss = { showEditDialog = false },
            onSave = { updatedTransaction ->
                TransactionRepository.updateTransaction(updatedTransaction)
                showEditDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFEB3B)
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { showEditDialog = true }) {
                        Text("Sửa", fontSize = 18.sp, color = Color(0xFF2196F3))
                    }
                    Text("|", color = Color.Gray, fontSize = 18.sp)
                    TextButton(onClick = { showDeleteDialog = true }) {
                        Text("Xóa", fontSize = 18.sp, color = Color.Red)
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            // --- Icon và tiêu đề ---
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF8E1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = transaction.icon,
                        contentDescription = null,
                        tint = transaction.categoryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))

            // ---- Các dòng thông tin ----
            DetailRow(label = "Kiểu", value = if (transaction.isIncome) "Thu nhập" else "Chi tiêu")
            DetailRow(label = "Số tiền", value = "%,d đ".format(transaction.amount.toInt()))
            DetailRow(label = "Thời gian", value = transaction.time)
            DetailRow(label = "Ghi chú", value = transaction.title)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {

        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    var title by remember { mutableStateOf(transaction.title) }
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var category by remember { mutableStateOf(transaction.category) }
    var time by remember { mutableStateOf(transaction.time) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sửa giao dịch") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Ghi chú") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Số tiền") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Danh mục") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Thời gian") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedTransaction = transaction.copy(
                        title = title,
                        amount = amount.toDoubleOrNull() ?: transaction.amount,
                        category = category,
                        time = time
                    )
                    onSave(updatedTransaction)
                }
            ) {
                Text("Lưu", color = Color(0xFF2196F3))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}