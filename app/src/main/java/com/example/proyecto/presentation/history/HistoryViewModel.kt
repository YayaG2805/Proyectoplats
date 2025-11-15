package com.example.proyecto.presentation.history

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.proyecto.domain.model.BudgetData

class HistoryViewModel : ViewModel() {

    private val _rows = MutableStateFlow<List<HistoryRow>>(emptyList())
    val rows: StateFlow<List<HistoryRow>> = _rows.asStateFlow()

    /** Agregar una fila al historial a partir de los datos del presupuesto */
    fun addFromBudget(data: BudgetData) {
        // Aquí tú puedes mejorar la lógica (mes real, estado real, etc.)
        val mes = "Mes actual"
        val ingresoQ = "Q %.0f".format(data.income)
        val ahorro = data.income * 0.10              // EJEMPLO: 10% de ahorro
        val ahorroQ = "Q %.0f".format(ahorro)
        val estado = "Cumplido"                      // Luego lo cambias según la lógica real

        val newRow = HistoryRow(
            mes = mes,
            ingreso = ingresoQ,
            ahorro = ahorroQ,
            estado = estado
        )

        _rows.value = _rows.value + newRow
    }

    fun clear() {
        _rows.value = emptyList()
    }
}
