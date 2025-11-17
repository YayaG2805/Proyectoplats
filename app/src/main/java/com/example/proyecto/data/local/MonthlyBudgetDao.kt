package com.example.proyecto.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyBudgetDao {

    @Insert
    suspend fun insert(budget: MonthlyBudgetEntity): Long

    @Query("SELECT * FROM monthly_budgets WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllByUser(userId: Long): Flow<List<MonthlyBudgetEntity>>

    @Query("SELECT * FROM monthly_budgets WHERE id = :budgetId")
    suspend fun getById(budgetId: Long): MonthlyBudgetEntity?

    @Query("SELECT * FROM monthly_budgets WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestByUser(userId: Long): MonthlyBudgetEntity?

    @Delete
    suspend fun delete(budget: MonthlyBudgetEntity)
}