class AppRepository(private val db: AppDatabase) {

    // --- User ---
    fun getUser() = db.userDao().getUser()
    suspend fun insertUser(user: User) = db.userDao().insert(user)
    suspend fun updateBalance(id: Int, balance: Double) = db.userDao().updateBalance(id, balance)

    // --- Category ---
    fun getCategories() = db.categoryDao().getAll()
    suspend fun addCategory(category: Category) = db.categoryDao().insert(category)
    suspend fun deleteCategory(category: Category) = db.categoryDao().delete(category)

    // --- Transaction ---
    fun getTransactions() = db.transactionDao().getAllTransactions()
    suspend fun addTransaction(tx: Transaction) = db.transactionDao().insert(tx)
    suspend fun updateTransaction(tx: Transaction) = db.transactionDao().update(tx)
    suspend fun deleteTransaction(tx: Transaction) = db.transactionDao().delete(tx)

    fun getTransactionsByRange(start: String, end: String) = db.transactionDao().getTransactionsByRange(start, end)
    fun getTransactionsByDay(date: String) = db.transactionDao().getTransactionsByDay(date)
    suspend fun getTotalAmount(type: String, start: String, end: String) = db.transactionDao().getTotalAmount(type, start, end)
}
