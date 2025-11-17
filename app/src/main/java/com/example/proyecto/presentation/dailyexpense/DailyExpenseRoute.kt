package com.example.proyecto.presentation.dailyexpense

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun DailyExpenseRoute(
    onBack: () -> Unit,
    vm: DailyExpenseViewModel = koinViewModel()
) {
    val expenses by vm.todayExpenses.collectAsState()
    val total by vm.totalToday.collectAsState()
    val suggestedLimit by vm.suggestedDailyLimit.collectAsState()

    DailyExpenseScreen(
        expenses = expenses,
        totalToday = total,
        suggestedDailyLimit = suggestedLimit,
        onAddExpense = vm::addExpense,
        onDeleteExpense = vm::deleteExpense,
        onBack = onBack
    )
}