package com.example.proyecto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_expenses")
data class DailyExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val date: String, // Formato: "2025-11-16"
    val category: String, // "COMIDA", "TRANSPORTE", "ENTRETENIMIENTO", "OTROS"
    val amount: Double,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)