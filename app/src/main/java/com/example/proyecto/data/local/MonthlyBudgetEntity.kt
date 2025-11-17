package com.example.proyecto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de presupuesto mensual guardado en la base de datos.
 */
@Entity(tableName = "monthly_budgets")
data class MonthlyBudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val month: String, // Formato: "2025-11" (año-mes)
    val income: Double,
    val rent: Double,
    val utilities: Double,
    val transport: Double,
    val other: Double,
    val modality: String, // EXTREMO, MEDIO, IMPREVISTO
    val createdAt: Long = System.currentTimeMillis()
)