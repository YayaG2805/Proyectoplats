package com.example.proyecto.presentation.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.MonthlyBudgetDao
import com.example.proyecto.domain.model.BudgetData
import com.example.proyecto.domain.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TipsBottomNavViewModel(
    private val monthlyBudgetDao: MonthlyBudgetDao
) : ViewModel() {

    private val _latestBudget = MutableStateFlow<BudgetData?>(null)
    val latestBudget: StateFlow<BudgetData?> = _latestBudget.asStateFlow()

    init {
        loadLatestBudget()
    }

    private fun loadLatestBudget() {
        viewModelScope.launch {
            val latest = monthlyBudgetDao.getLatestByUser(UserSession.userId)
            if (latest != null) {
                _latestBudget.value = BudgetData(
                    income = latest.income,
                    rent = latest.rent,
                    utilities = latest.utilities,
                    transport = latest.transport,
                    other = latest.other,
                    modality = latest.modality
                )
            }
        }
    }
}