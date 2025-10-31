package com.example.proyecto.presentation.flow

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.proyecto.domain.model.BudgetData

class BudgetFlowViewModel : ViewModel() {
    private val _pending = MutableStateFlow<BudgetData?>(null)
    val pending = _pending.asStateFlow()

    fun set(data: BudgetData) { _pending.value = data }
    fun clear() { _pending.value = null }
}
