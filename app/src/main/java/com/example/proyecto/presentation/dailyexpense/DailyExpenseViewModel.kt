package com.example.proyecto.presentation.dailyexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.DailyExpenseDao
import com.example.proyecto.data.local.DailyExpenseEntity
import com.example.proyecto.domain.model.DailyExpense
import com.example.proyecto.domain.model.ExpenseCategory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyExpenseViewModel(
    private val dao: DailyExpenseDao
) : ViewModel() {

    // TODO: En producción, obtener el userId real del usuario logueado
    private val currentUserId = 1L

    private val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    val todayExpenses: StateFlow<List<DailyExpense>> = dao
        .getExpensesByDate(currentUserId, today)
        .map { entities ->
            entities.map { it.toDomain() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalToday: StateFlow<Double> = todayExpenses
        .map { expenses -> expenses.sumOf { it.amount } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    fun addExpense(category: ExpenseCategory, amount: Double, description: String) {
        viewModelScope.launch {
            dao.insert(
                DailyExpenseEntity(
                    userId = currentUserId,
                    date = today,
                    category = category.name,
                    amount = amount,
                    description = description
                )
            )
        }
    }

    fun deleteExpense(expenseId: Long) {
        viewModelScope.launch {
            dao.delete(expenseId)
        }
    }

    private fun DailyExpenseEntity.toDomain() = DailyExpense(
        id = id,
        date = date,
        category = ExpenseCategory.valueOf(category),
        amount = amount,
        description = description
    )
}