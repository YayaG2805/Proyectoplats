package com.example.proyecto.presentation.flow

import androidx.compose.runtime.Composable
import com.example.proyecto.domain.model.BudgetData

@Composable
fun SummaryRoute(
    data: BudgetData,
    onSeeTips: () -> Unit,
    onSaveToHistory: () -> Unit,
    onContinue: () -> Unit
) {
    SummaryScreen(
        data = data,
        onSeeTips = onSeeTips,
        onSaveToHistory = onSaveToHistory,
        onContinue = onContinue
    )
}
