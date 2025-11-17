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

    // ===== FIX: Agregar trigger para forzar actualización =====
    private val _refreshTrigger = MutableStateFlow(0L)

    // Presupuesto del mes actual (el único editable)
    val currentMonthBudget: StateFlow<MonthlyBudgetEntity?> = combine(
        userPreferences.userId,
        _refreshTrigger
    ) { userId, _ -> userId }
        .filterNotNull()
        .flatMapLatest { userId ->
            monthlyBudgetDao.getAllByUser(userId)
                .map { budgets ->
                    budgets.firstOrNull { it.month == currentMonth }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Historial de meses anteriores (read-only)
    val previousMonths: StateFlow<List<HistoryRow>> = combine(
        userPreferences.userId,
        _refreshTrigger
    ) { userId, _ -> userId }
        .filterNotNull()
        .flatMapLatest { userId ->
            monthlyBudgetDao.getAllByUser(userId)
                .map { budgets ->
                    budgets.filter { it.month != currentMonth }
                        .map { it.toHistoryRow() }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Todas las filas (para compatibilidad con código existente)
    val rows: StateFlow<List<HistoryRow>> = combine(
        userPreferences.userId,
        _refreshTrigger
    ) { userId, _ -> userId }
        .filterNotNull()
        .flatMapLatest { userId ->
            monthlyBudgetDao.getAllByUser(userId)
                .map { budgets ->
                    budgets.map { it.toHistoryRow() }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Crear o actualizar el presupuesto del mes actual.
     */
    fun addFromBudget(data: BudgetData) {
        viewModelScope.launch {
            userPreferences.userId.first()?.let { userId ->
                val existing = monthlyBudgetDao.getByUserAndMonth(userId, currentMonth)

                if (existing != null) {
                    // ACTUALIZAR existente
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
                    // Crear nuevo
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

                // ===== FIX: Forzar actualización de flows =====
                _refreshTrigger.value = System.currentTimeMillis()
            }
        }
    }

    /**
     * Agregar ingreso extra al presupuesto del mes actual.
     */
    fun addExtraIncome(amount: Double) {
        viewModelScope.launch {
            userPreferences.userId.first()?.let { userId ->
                val current = monthlyBudgetDao.getByUserAndMonth(userId, currentMonth)
                if (current != null) {
                    monthlyBudgetDao.update(
                        current.copy(income = current.income + amount)
                    )
                    // ===== FIX: Forzar actualización =====
                    _refreshTrigger.value = System.currentTimeMillis()
                }
            }
        }
    }

    /**
     * Actualizar modalidad del presupuesto actual.
     */
    fun updateModality(newModality: String) {
        viewModelScope.launch {
            userPreferences.userId.first()?.let { userId ->
                val current = monthlyBudgetDao.getByUserAndMonth(userId, currentMonth)
                if (current != null) {
                    monthlyBudgetDao.update(
                        current.copy(modality = newModality)
                    )
                    // ===== FIX: Forzar actualización =====
                    _refreshTrigger.value = System.currentTimeMillis()
                }
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
                // ===== FIX: Forzar actualización =====
                _refreshTrigger.value = System.currentTimeMillis()
            }
        }
    }

    /**
     * Limpiar historial al cerrar sesión.
     * ===== FIX: Ahora realmente resetea los flows =====
     */
    fun clear() {
        _refreshTrigger.value = System.currentTimeMillis()
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