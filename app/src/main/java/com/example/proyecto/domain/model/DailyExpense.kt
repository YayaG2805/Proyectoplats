package com.example.proyecto.domain.model

data class DailyExpense(
    val id: Long = 0,
    val date: String,
    val category: ExpenseCategory,
    val amount: Double,
    val description: String
)

enum class ExpenseCategory(val displayName: String) {
    COMIDA("Comida"),
    TRANSPORTE("Transporte"),
    ENTRETENIMIENTO("Entretenimiento"),
    SERVICIOS("Servicios"),
    SALUD("Salud"),
    OTROS("Otros")
}