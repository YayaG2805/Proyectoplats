package com.example.proyecto.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyExpenseDao {

    @Insert
    suspend fun insert(expense: DailyExpenseEntity): Long

    @Query("SELECT * FROM daily_expenses WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    fun getExpensesByDate(userId: Long, date: String): Flow<List<DailyExpenseEntity>>

    @Query("SELECT * FROM daily_expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, timestamp DESC")
    fun getExpensesByDateRange(userId: Long, startDate: String, endDate: String): Flow<List<DailyExpenseEntity>>

    @Query("SELECT SUM(amount) FROM daily_expenses WHERE userId = :userId AND date = :date")
    suspend fun getTotalForDate(userId: Long, date: String): Double?

    @Query("SELECT SUM(amount) FROM daily_expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalForDateRange(userId: Long, startDate: String, endDate: String): Double?

    @Query("SELECT * FROM daily_expenses WHERE userId = :userId ORDER BY date DESC, timestamp DESC LIMIT 50")
    fun getRecentExpenses(userId: Long): Flow<List<DailyExpenseEntity>>

    @Query("DELETE FROM daily_expenses WHERE id = :expenseId")
    suspend fun delete(expenseId: Long)
}