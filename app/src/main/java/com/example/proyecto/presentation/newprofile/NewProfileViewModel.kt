package com.example.proyecto.presentation.newprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.domain.model.BudgetData
import com.example.proyecto.presentation.history.HistoryViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    private val historyViewModel: HistoryViewModel
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
     * Valida y guarda el presupuesto en el historial.
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

        // Construir BudgetData
        val budgetData = BudgetData(
            income = income,
            rent = state.rent.toDoubleOrNull() ?: 0.0,
            utilities = state.utilities.toDoubleOrNull() ?: 0.0,
            transport = state.transport.toDoubleOrNull() ?: 0.0,
            other = state.other.toDoubleOrNull() ?: 0.0,
            modality = state.modality
        )

        // Guardar en el historial
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                // Agregar al historial
                historyViewModel.addFromBudget(budgetData)

                // Generar ID único (en producción sería de la BD)
                val newId = System.currentTimeMillis()

                // Callback de éxito
                onSaved(newId)

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