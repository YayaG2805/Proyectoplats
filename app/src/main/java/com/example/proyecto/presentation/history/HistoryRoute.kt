package com.example.proyecto.presentation.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryRoute(onAddNew: () -> Unit) {
    val vm: HistoryViewModel = koinViewModel()
    val rows = vm.rows.collectAsState().value

    HistoryScreen(rows = rows, onAddNew = onAddNew)
}