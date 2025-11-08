import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
@Dao
interface TransactionDao {

    // --- CRUD ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    // --- Lấy danh sách toàn bộ giao dịch ---
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    // --- Lọc giao dịch theo khoảng thời gian ---
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByRange(startDate: String, endDate: String): Flow<List<Transaction>>

    // --- Lọc theo loại (thu/chi) ---
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<Transaction>>

    // --- Thống kê tổng chi tiêu/thu nhập trong khoảng ---
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date BETWEEN :start AND :end")
    suspend fun getTotalAmount(type: String, start: String, end: String): Double?

    // --- Chi tiết theo ngày cụ thể ---
    @Query("SELECT * FROM transactions WHERE date = :date ORDER BY id DESC")
    fun getTransactionsByDay(date: String): Flow<List<Transaction>>
}
