package com.example.proyecto.presentation.flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.proyecto.presentation.history.HistoryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ModalityRoute(
    selected: String,
    onSelectedChange: (String) -> Unit,
    onContinue: () -> Unit,
    onOpenHistory: () -> Unit
) {

    val historyVM: HistoryViewModel = koinViewModel()
    val rows by historyVM.rows.collectAsState()
    val hasHistory = rows.isNotEmpty()

    ModalityScreen(
        selected = selected,
        onSelect = onSelectedChange,
        onContinue = onContinue,
        onOpenHistory = onOpenHistory,
        showHistory = hasHistory
    )
}
