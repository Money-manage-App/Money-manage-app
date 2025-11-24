package com.example.money_manage_app.data.local.database.dao

import androidx.room.*
import com.example.money_manage_app.data.local.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getUser(userId: String): Flow<User?>

    @Query("DELETE FROM User WHERE userId = :userId")
    suspend fun deleteUser(userId: String)

    @Update
    suspend fun updateUser(user: User)

}
