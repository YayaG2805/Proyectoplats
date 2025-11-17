package com.example.proyecto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        DailyExpenseEntity::class,
        MonthlyBudgetEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun dailyExpenseDao(): DailyExpenseDao
    abstract fun monthlyBudgetDao(): MonthlyBudgetDao
}