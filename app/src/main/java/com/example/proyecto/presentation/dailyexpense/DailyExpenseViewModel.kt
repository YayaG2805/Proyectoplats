package com.example.proyecto.presentation.dailyexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.DailyExpenseDao
import com.example.proyecto.data.local.DailyExpenseEntity
import com.example.proyecto.data.local.MonthlyBudgetDao
import com.example.proyecto.data.local.UserPreferences
import com.example.proyecto.domain.model.DailyExpense
import com.example.proyecto.domain.model.ExpenseCategory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyExpenseViewModel(
    private val dao: DailyExpenseDao,
    private val userPreferences: UserPreferences,
    private val monthlyBudgetDao: MonthlyBudgetDao
) : ViewModel() {

    private val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    private val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    private val startOfMonth = "${currentMonth}-01"
    private val endOfMonth = LocalDate.now().withDayOfMonth(
        LocalDate.now().lengthOfMonth()
    ).format(DateTimeFormatter.ISO_LOCAL_DATE)

    // Obtener userId dinámicamente del DataStore
    private val currentUserId: StateFlow<Long> = userPreferences.userId
        .map { it ?: 1L } // Default 1L si no hay usuario
        .stateIn(viewModelScope, SharingStarted.Eagerly, 1L)

    val todayExpenses: StateFlow<List<DailyExpense>> = currentUserId.flatMapLatest { userId ->
        dao.getExpensesByDate(userId, today)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }.stateIn(
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

    /**
     * Límite diario sugerido calculado desde el presupuesto mensual actual.
     */
    val suggestedDailyLimit: StateFlow<Double> = currentUserId.flatMapLatest { userId ->
        combine(
            monthlyBudgetDao.getAllByUser(userId),
            dao.getExpensesByDateRange(userId, startOfMonth, endOfMonth)
        ) { budgets, monthExpenses ->
            val currentBudget = budgets.firstOrNull()

            if (currentBudget != null) {
                val fixedExpenses = currentBudget.rent +
                        currentBudget.utilities +
                        currentBudget.transport +
                        currentBudget.other

                val plannedBalance = currentBudget.income - fixedExpenses
                val monthlyDailyTotal = monthExpenses.sumOf { it.amount }
                val actualBalance = plannedBalance - monthlyDailyTotal

                val daysInMonth = LocalDate.now().lengthOfMonth()
                val daysElapsed = LocalDate.now().dayOfMonth
                val daysLeft = daysInMonth - daysElapsed

                // Calcular límite diario
                if (daysLeft > 0 && actualBalance > 0) {
                    actualBalance / daysLeft
                } else {
                    0.0
                }
            } else {
                0.0 // No hay presupuesto
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun addExpense(category: ExpenseCategory, amount: Double, description: String) {
        viewModelScope.launch {
            dao.insert(
                DailyExpenseEntity(
                    userId = currentUserId.value,
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