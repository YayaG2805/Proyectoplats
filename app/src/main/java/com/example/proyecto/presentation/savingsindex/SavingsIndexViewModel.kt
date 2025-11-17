package com.example.proyecto.presentation.savingsindex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.MonthlyBudgetDao
import com.example.proyecto.data.local.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class SavingsIndexUiState(
    val hasData: Boolean = false,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val totalSavings: Double = 0.0,
    val totalRent: Double = 0.0,
    val totalUtilities: Double = 0.0,
    val totalTransport: Double = 0.0,
    val totalOther: Double = 0.0,
    val savingPercentage: Double = 0.0,
    val savingStatus: String = "Sin datos",
    val monthlyTrend: List<MonthTrend> = emptyList(),
    val recommendations: List<String> = emptyList()
)

class SavingsIndexViewModel(
    private val monthlyBudgetDao: MonthlyBudgetDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<SavingsIndexUiState> = userPreferences.userId
        .filterNotNull()
        .flatMapLatest { userId ->
            monthlyBudgetDao.getAllByUser(userId).map { budgets ->
                if (budgets.isEmpty()) {
                    SavingsIndexUiState(hasData = false)
                } else {
                    val totalIncome = budgets.sumOf { it.income }
                    val totalRent = budgets.sumOf { it.rent }
                    val totalUtilities = budgets.sumOf { it.utilities }
                    val totalTransport = budgets.sumOf { it.transport }
                    val totalOther = budgets.sumOf { it.other }
                    val totalExpenses = totalRent + totalUtilities + totalTransport + totalOther
                    val totalSavings = totalIncome - totalExpenses

                    val savingPercentage = if (totalIncome > 0) {
                        (totalSavings / totalIncome) * 100
                    } else 0.0

                    val savingStatus = when {
                        savingPercentage >= 30 -> "Excelente"
                        savingPercentage >= 20 -> "Muy bueno"
                        savingPercentage >= 10 -> "Bueno"
                        savingPercentage >= 5 -> "Regular"
                        else -> "Bajo"
                    }

                    // Tendencia mensual
                    val monthlyTrend = budgets.sortedBy { it.month }.mapIndexed { index, budget ->
                        val expenses = budget.rent + budget.utilities + budget.transport + budget.other
                        val savings = budget.income - expenses
                        val percentage = if (budget.income > 0) (savings / budget.income) * 100 else 0.0

                        val trend = if (index > 0) {
                            val prevBudget = budgets[index - 1]
                            val prevExpenses = prevBudget.rent + prevBudget.utilities + prevBudget.transport + prevBudget.other
                            val prevSavings = prevBudget.income - prevExpenses
                            val prevPercentage = if (prevBudget.income > 0) (prevSavings / prevBudget.income) * 100 else 0.0
                            if (percentage >= prevPercentage) "up" else "down"
                        } else "up"

                        val monthName = try {
                            val date = LocalDate.parse("${budget.month}-01")
                            date.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale("es", "GT")))
                                .replaceFirstChar { it.uppercase() }
                        } catch (e: Exception) {
                            budget.month
                        }

                        MonthTrend(monthName, percentage, trend)
                    }

                    // Recomendaciones basadas en los datos
                    val recommendations = mutableListOf<String>()

                    if (savingPercentage < 10) {
                        recommendations.add("Tu índice de ahorro está por debajo del 10%. Intenta reducir gastos innecesarios.")
                    }

                    val rentPercentage = if (totalIncome > 0) (totalRent / totalIncome) * 100 else 0.0
                    if (rentPercentage > 30) {
                        recommendations.add("Tu renta representa el ${rentPercentage.toInt()}% de tus ingresos. Lo ideal es mantenerla bajo el 30%.")
                    }

                    val transportPercentage = if (totalIncome > 0) (totalTransport / totalIncome) * 100 else 0.0
                    if (transportPercentage > 15) {
                        recommendations.add("Estás gastando mucho en transporte (${transportPercentage.toInt()}%). Considera opciones más económicas.")
                    }

                    val otherPercentage = if (totalIncome > 0) (totalOther / totalIncome) * 100 else 0.0
                    if (otherPercentage > 20) {
                        recommendations.add("Tus 'otros gastos' son el ${otherPercentage.toInt()}% de tus ingresos. Identifica gastos hormiga.")
                    }

                    if (savingPercentage >= 20) {
                        recommendations.add("¡Excelente trabajo! Estás ahorrando más del 20% de tus ingresos.")
                    }

                    if (recommendations.isEmpty()) {
                        recommendations.add("Tu presupuesto está bien balanceado. Sigue así.")
                    }

                    SavingsIndexUiState(
                        hasData = true,
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        totalSavings = totalSavings,
                        totalRent = totalRent,
                        totalUtilities = totalUtilities,
                        totalTransport = totalTransport,
                        totalOther = totalOther,
                        savingPercentage = savingPercentage,
                        savingStatus = savingStatus,
                        monthlyTrend = monthlyTrend,
                        recommendations = recommendations
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavingsIndexUiState(hasData = false)
        )
}