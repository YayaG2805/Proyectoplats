package com.example.proyecto.presentation.flow

import androidx.lifecycle.ViewModel
import com.example.proyecto.domain.model.BudgetData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BudgetFormViewModel : ViewModel() {

    data class BudgetFormUi(
        val income: String = "",
        val rent: String = "",
        val utilities: String = "",
        val transport: String = "",
        val other: String = "",
        val modality: String = "MEDIO" // puedes cambiarlo desde la UI si tienes selector
    )

    private val _uiState = MutableStateFlow(BudgetFormUi())
    val uiState: StateFlow<BudgetFormUi> = _uiState.asStateFlow()

    fun onIncomeChange(value: String) {
        _uiState.value = _uiState.value.copy(income = value)
    }

    fun onRentChange(value: String) {
        _uiState.value = _uiState.value.copy(rent = value)
    }

    fun onUtilitiesChange(value: String) {
        _uiState.value = _uiState.value.copy(utilities = value)
    }

    fun onTransportChange(value: String) {
        _uiState.value = _uiState.value.copy(transport = value)
    }

    fun onOtherChange(value: String) {
        _uiState.value = _uiState.value.copy(other = value)
    }

    fun onModalityChange(value: String) {
        _uiState.value = _uiState.value.copy(modality = value)
    }

    /** Construye el modelo de dominio con los nombres en inglés (consistente con el resto del proyecto). */
    fun buildBudgetData(): BudgetData {
        val income = _uiState.value.income.toDoubleOrNull() ?: 0.0
        val rent = _uiState.value.rent.toDoubleOrNull() ?: 0.0
        val utilities = _uiState.value.utilities.toDoubleOrNull() ?: 0.0
        val transport = _uiState.value.transport.toDoubleOrNull() ?: 0.0
        val other = _uiState.value.other.toDoubleOrNull() ?: 0.0
        return BudgetData(
            income = income,
            rent = rent,
            utilities = utilities,
            transport = transport,
            other = other,
            modality = _uiState.value.modality
        )
    }
}
