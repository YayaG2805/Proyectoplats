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

    private val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    // Presupuesto del mes actual (el único editable)
    private val _currentMonthBudget = MutableStateFlow<MonthlyBudgetEntity?>(null)
    val currentMonthBudget: StateFlow<MonthlyBudgetEntity?> = _currentMonthBudget.asStateFlow()

    // Historial de meses anteriores (read-only)
    private val _previousMonths = MutableStateFlow<List<HistoryRow>>(emptyList())
    val previousMonths: StateFlow<List<HistoryRow>> = _previousMonths.asStateFlow()

    // Todas las filas (para compatibilidad con código existente)
    private val _rows = MutableStateFlow<List<HistoryRow>>(emptyList())
    val rows: StateFlow<List<HistoryRow>> = _rows.asStateFlow()

    init {
        loadHistoryFromDatabase()
    }

    /**
     * Carga el historial desde la base de datos.
     * Separa el mes actual de los meses anteriores.
     * CORREGIDO: Usa StateFlow correctamente sin llamar a clear()
     */
    private fun loadHistoryFromDatabase() {
        viewModelScope.launch {
            userPreferences.userId
                .filterNotNull() // Solo procesa cuando hay userId
                .flatMapLatest { userId ->
                    monthlyBudgetDao.getAllByUser(userId)
                }
                .collect { budgets ->
                    // Separar mes actual de históricos
                    val current = budgets.firstOrNull { it.month == currentMonth }
                    val previous = budgets.filter { it.month != currentMonth }

                    _currentMonthBudget.value = current
                    _previousMonths.value = previous.map { it.toHistoryRow() }
                    _rows.value = budgets.map { it.toHistoryRow() }
                }
        }
    }

    /**
     * Crear o actualizar el presupuesto del mes actual.
     */
    fun addFromBudget(data: BudgetData) {
        viewModelScope.launch {
            userPreferences.userId.first()?.let { userId ->
                val existing = _currentMonthBudget.value

                if (existing != null) {
                    // ACTUALIZAR existente - Usar update()
                    monthlyBudgetDao.update(
                        existing.copy(
                            income = data.income,
                            rent = data.rent,
                            utilities = data.utilities,
                            transport = data.transport,
                            other = data.other,
                            modality = data.modality
                        )
                    )
                } else {
                    // Crear nuevo - Usar insert()
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
                }
            }
        }
    }

    /**
     * Agregar ingreso extra al presupuesto del mes actual.
     */
    fun addExtraIncome(amount: Double) {
        viewModelScope.launch {
            val current = _currentMonthBudget.value
            if (current != null) {
                monthlyBudgetDao.update(
                    current.copy(income = current.income + amount)
                )
            }
        }
    }

    /**
     * Actualizar modalidad del presupuesto actual.
     */
    fun updateModality(newModality: String) {
        viewModelScope.launch {
            val current = _currentMonthBudget.value
            if (current != null) {
                monthlyBudgetDao.update(
                    current.copy(modality = newModality)
                )
            }
        }
    }

    /**
     * Eliminar un presupuesto mensual (solo meses anteriores).
     */
    fun deleteById(budgetId: Long) {
        viewModelScope.launch {
            val budget = monthlyBudgetDao.getById(budgetId)
            if (budget != null && budget.month != currentMonth) {
                monthlyBudgetDao.delete(budget)
            }
        }
    }

    /**
     * Limpiar historial solo para logout.
     * CORREGIDO: Ya no se llama al cambiar contraseña.
     */
    fun clear() {
        _rows.value = emptyList()
        _currentMonthBudget.value = null
        _previousMonths.value = emptyList()
    }

    /**
     * Convierte MonthlyBudgetEntity a HistoryRow para la UI.
     */
    private fun MonthlyBudgetEntity.toHistoryRow(): HistoryRow {
        val nf = NumberFormat.getCurrencyInstance(Locale("es", "GT"))

        val totalExpenses = rent + utilities + transport + other
        val balance = income - totalExpenses
        val savingPercentage = if (income > 0) (balance / income) * 100 else 0.0

        val estado = when (modality) {
            "EXTREMO" -> if (savingPercentage >= 30) "Cumplido" else if (savingPercentage >= 15) "Parcial" else "No cumplido"
            "MEDIO" -> if (savingPercentage >= 15) "Cumplido" else if (savingPercentage >= 8) "Parcial" else "No cumplido"
            "IMPREVISTO" -> if (savingPercentage >= 20) "Cumplido" else if (savingPercentage >= 10) "Parcial" else "No cumplido"
            else -> if (savingPercentage >= 10) "Cumplido" else "Parcial"
        }

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