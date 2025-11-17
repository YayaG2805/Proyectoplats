package com.example.proyecto.presentation.budgetstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.DailyExpenseDao
import com.example.proyecto.data.local.MonthlyBudgetDao
import com.example.proyecto.data.local.UserPreferences
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class BudgetStatusUiState(
    // Presupuesto mensual
    val monthlyIncome: Double = 0.0,
    val monthlyFixedExpenses: Double = 0.0,
    val monthlyPlannedBalance: Double = 0.0,
    val modality: String = "MEDIO",

    // Gastos diarios del mes
    val dailyExpensesThisMonth: Double = 0.0,
    val dailyExpensesCount: Int = 0,

    // Cálculos
    val actualBalance: Double = 0.0,
    val percentageSpent: Double = 0.0,
    val isOverBudget: Boolean = false,
    val daysLeftInMonth: Int = 0,
    val averageDailySpending: Double = 0.0,
    val suggestedDailyLimit: Double = 0.0,

    // Estado
    val hasMonthlyBudget: Boolean = false,
    val currentMonth: String = ""
)

class BudgetStatusViewModel(
    private val monthlyBudgetDao: MonthlyBudgetDao,
    private val dailyExpenseDao: DailyExpenseDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    private val today = LocalDate.now()
    private val startOfMonth = "${currentMonth}-01"
    private val endOfMonth = LocalDate.now().withDayOfMonth(
        LocalDate.now().lengthOfMonth()
    ).format(DateTimeFormatter.ISO_LOCAL_DATE)

    val uiState: StateFlow<BudgetStatusUiState> = userPreferences.userId
        .filterNotNull()
        .flatMapLatest { userId ->
            combine(
                monthlyBudgetDao.getAllByUser(userId),
                dailyExpenseDao.getExpensesByDateRange(userId, startOfMonth, endOfMonth)
            ) { budgets, expenses ->
                val currentBudget = budgets.firstOrNull()

                if (currentBudget == null) {
                    BudgetStatusUiState(
                        hasMonthlyBudget = false,
                        currentMonth = currentMonth
                    )
                } else {
                    val fixedExpenses = currentBudget.rent +
                            currentBudget.utilities +
                            currentBudget.transport +
                            currentBudget.other

                    val plannedBalance = currentBudget.income - fixedExpenses
                    val dailyTotal = expenses.sumOf { it.amount }
                    val actualBalance = plannedBalance - dailyTotal

                    val daysInMonth = today.lengthOfMonth()
                    val daysElapsed = today.dayOfMonth
                    val daysLeft = daysInMonth - daysElapsed

                    val avgDaily = if (daysElapsed > 0) dailyTotal / daysElapsed else 0.0
                    val suggestedLimit = if (daysLeft > 0) actualBalance / daysLeft else 0.0

                    val percentSpent = if (plannedBalance > 0) {
                        (dailyTotal / plannedBalance) * 100
                    } else 0.0

                    BudgetStatusUiState(
                        monthlyIncome = currentBudget.income,
                        monthlyFixedExpenses = fixedExpenses,
                        monthlyPlannedBalance = plannedBalance,
                        modality = currentBudget.modality,
                        dailyExpensesThisMonth = dailyTotal,
                        dailyExpensesCount = expenses.size,
                        actualBalance = actualBalance,
                        percentageSpent = percentSpent,
                        isOverBudget = actualBalance < 0,
                        daysLeftInMonth = daysLeft,
                        averageDailySpending = avgDaily,
                        suggestedDailyLimit = if (suggestedLimit > 0) suggestedLimit else 0.0,
                        hasMonthlyBudget = true,
                        currentMonth = formatMonth(currentMonth)
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BudgetStatusUiState(currentMonth = formatMonth(currentMonth))
        )

    private fun formatMonth(month: String): String {
        return try {
            val date = LocalDate.parse("$month-01")
            date.format(DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale("es", "GT")))
                .replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            month
        }
    }
}