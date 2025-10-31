package com.example.proyecto.presentation.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun HistoryRoute(onAddNew: ()->Unit) {
    val rows = remember {
        listOf(
            HistoryRow("Marzo", "Q 7000", "Q 700", "Cumplido"),
            HistoryRow("Febrero", "Q 7000", "Q 700", "Parcial"),
            HistoryRow("Enero", "Q 7000", "Q 700", "No cumplido"),
            HistoryRow("Diciembre", "Q 6800", "Q 800", "Cumplido"),
        )
    }
    HistoryScreen(rows = rows, onAddNew = onAddNew)
}
