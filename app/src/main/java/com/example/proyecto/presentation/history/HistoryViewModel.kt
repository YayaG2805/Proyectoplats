package com.example.proyecto.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.MonthlyBudgetDao
import com.example.proyecto.data.local.MonthlyBudgetEntity
import com.example.proyecto.data.local.UserPreferences
import com.example.proyecto.domain.model.BudgetData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoryViewModel(
    private val monthlyBudgetDao: MonthlyBudgetDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _rows = MutableStateFlow<List<HistoryRow>>(emptyList())
    val rows: StateFlow<List<HistoryRow>> = _rows.asStateFlow()

    init {
        loadHistoryFromDatabase()
    }

    /**
     * Carga el historial desde la base de datos.
     */
    private fun loadHistoryFromDatabase() {
        viewModelScope.launch {
            userPreferences.userId.collect { userId ->
                if (userId != null) {
                    monthlyBudgetDao.getAllByUser(userId).collect { budgets ->
                        _rows.value = budgets.map { it.toHistoryRow() }
                    }
                }
            }
        }
    }

    /**
     * Agregar una fila al historial a partir de los datos del presupuesto.
     * Ahora guarda en la base de datos.
     */
    fun addFromBudget(data: BudgetData) {
        viewModelScope.launch {
            userPreferences.userId.collect { userId ->
                if (userId != null) {
                    val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

                    monthlyBudgetDao.insert(
                        MonthlyBudgetEntity(
                            userId = userId,
                            month = currentMonth,
                            income = data.income,
                            rent = data.rent,
                            utilities = data.utilities,
                            transport = data.transport,
                            other = data.other,
                            modality = data.modality
                        )
                    )
                    // No necesitamos actualizar manualmente porque el Flow lo hace automáticamente
                }
            }
        }
    }

    /**
     * Eliminar un presupuesto mensual del historial.
     */
    fun deleteById(budgetId: Long) {
        viewModelScope.launch {
            val budget = monthlyBudgetDao.getById(budgetId)
            if (budget != null) {
                monthlyBudgetDao.delete(budget)
            }
        }
    }

    fun clear() {
        // Esta función ya no limpia la lista, porque viene de la BD
        // Solo se usa al cerrar sesión para limpiar el estado local si es necesario
        _rows.value = emptyList()
    }

    /**
     * Convierte MonthlyBudgetEntity a HistoryRow para la UI.
     */
    private fun MonthlyBudgetEntity.toHistoryRow(): HistoryRow {
        val nf = NumberFormat.getCurrencyInstance(Locale("es", "GT"))

        // Calcular ahorro según modalidad
        val totalExpenses = rent + utilities + transport + other
        val balance = income - totalExpenses
        val savingPercentage = if (income > 0) (balance / income) * 100 else 0.0

        // Determinar estado basado en la modalidad
        val estado = when (modality) {
            "EXTREMO" -> if (savingPercentage >= 30) "Cumplido" else if (savingPercentage >= 15) "Parcial" else "No cumplido"
            "MEDIO" -> if (savingPercentage >= 15) "Cumplido" else if (savingPercentage >= 8) "Parcial" else "No cumplido"
            "IMPREVISTO" -> if (savingPercentage >= 20) "Cumplido" else if (savingPercentage >= 10) "Parcial" else "No cumplido"
            else -> if (savingPercentage >= 10) "Cumplido" else "Parcial"
        }

        // Formatear mes (de "2025-11" a "Noviembre 2025")
        val monthFormatted = try {
            val date = LocalDate.parse("$month-01")
            date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "GT")))
                .replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            month
        }

        return HistoryRow(
            id = id,
            mes = monthFormatted,
            ingreso = nf.format(income),
            ahorro = nf.format(balance),
            estado = estado
        )
    }
}