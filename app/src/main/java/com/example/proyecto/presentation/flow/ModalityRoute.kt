package com.example.proyecto.presentation.flow

import androidx.compose.runtime.*

@Composable
fun ModalityRoute(
    onContinue: () -> Unit,
    onOpenHistory: () -> Unit
) {
    var selected by remember { mutableStateOf("EXTREMO") }
    ModalityScreen(
        selected = selected,
        onSelect = { selected = it },
        onContinue = onContinue,
        onOpenHistory = onOpenHistory
    )
}
