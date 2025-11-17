package com.example.proyecto.presentation.newprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.MonthlyBudgetDao
import com.example.proyecto.data.local.MonthlyBudgetEntity
import com.example.proyecto.data.local.UserPreferences
import com.example.proyecto.domain.model.BudgetData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class NewProfileUiState(
    val income: String = "",
    val rent: String = "",
    val utilities: String = "",
    val transport: String = "",
    val other: String = "",
    val modality: String = "MEDIO",
    val incomeError: String? = null,
    val error: String? = null,
    val isSaving: Boolean = false
)

class NewProfileViewModel(
    private val monthlyBudgetDao: MonthlyBudgetDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewProfileUiState())
    val uiState: StateFlow<NewProfileUiState> = _uiState.asStateFlow()

    fun onIncomeChange(value: String) {
        _uiState.update { it.copy(income = value, incomeError = null, error = null) }
    }

    fun onRentChange(value: String) {
        _uiState.update { it.copy(rent = value, error = null) }
    }

    fun onUtilitiesChange(value: String) {
        _uiState.update { it.copy(utilities = value, error = null) }
    }

    fun onTransportChange(value: String) {
        _uiState.update { it.copy(transport = value, error = null) }
    }

    fun onOtherChange(value: String) {
        _uiState.update { it.copy(other = value, error = null) }
    }

    fun setModality(modality: String) {
        _uiState.update { it.copy(modality = modality) }
    }

    /**
     * Valida y guarda el presupuesto en la base de datos.
     * CORREGIDO: Ahora guarda directamente en la BD sin depender de HistoryViewModel.
     */
    fun save(onSaved: (Long) -> Unit) {
        val state = _uiState.value

        // Validar ingreso obligatorio
        if (state.income.isBlank()) {
            _uiState.update { it.copy(incomeError = "El ingreso es obligatorio") }
            return
        }

        val income = state.income.toDoubleOrNull()
        if (income == null || income <= 0) {
            _uiState.update { it.copy(incomeError = "Ingresa un monto válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                // Obtener userId actual
                val userId = userPreferences.userId.first()
                if (userId == null) {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = "No hay sesión activa. Por favor, inicia sesión."
                        )
                    }
                    return@launch
                }

                // Mes actual
                val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

                // Verificar si ya existe un presupuesto para este mes
                val existing = monthlyBudgetDao.getByUserAndMonth(userId, currentMonth)

                if (existing != null) {
                    // ACTUALIZAR presupuesto existente
                    monthlyBudgetDao.update(
                        existing.copy(
                            income = income,
                            rent = state.rent.toDoubleOrNull() ?: 0.0,
                            utilities = state.utilities.toDoubleOrNull() ?: 0.0,
                            transport = state.transport.toDoubleOrNull() ?: 0.0,
                            other = state.other.toDoubleOrNull() ?: 0.0,
                            modality = state.modality
                        )
                    )
                } else {
                    // CREAR nuevo presupuesto
                    monthlyBudgetDao.insert(
                        MonthlyBudgetEntity(
                            userId = userId,
                            month = currentMonth,
                            income = income,
                            rent = state.rent.toDoubleOrNull() ?: 0.0,
                            utilities = state.utilities.toDoubleOrNull() ?: 0.0,
                            transport = state.transport.toDoubleOrNull() ?: 0.0,
                            other = state.other.toDoubleOrNull() ?: 0.0,
                            modality = state.modality
                        )
                    )
                }

                // Callback de éxito
                onSaved(System.currentTimeMillis())

                // Limpiar formulario
                _uiState.update { NewProfileUiState(modality = state.modality) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Error al guardar: ${e.message}"
                    )
                }
            }
        }
    }
}