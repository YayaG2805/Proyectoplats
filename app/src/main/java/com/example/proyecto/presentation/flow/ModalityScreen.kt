@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.proyecto.presentation.flow

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun ModalityScreen(
    selected: String,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Selecciona tu modalidad", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        ModalityCard("EXTREMO","Maximiza tu ahorro reduciendo gastos al mínimo.", selected, onSelect)
        Spacer(Modifier.height(8.dp))
        ModalityCard("MEDIO","Un balance entre ahorro y gastos personales.", selected, onSelect)
        Spacer(Modifier.height(8.dp))
        ModalityCard("IMPREVISTO","Reserva un fondo para imprevistos.", selected, onSelect)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Continuar") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onOpenHistory, modifier = Modifier.fillMaxWidth()) { Text("Ver historial") }
    }
}

@Composable
private fun ModalityCard(
    title: String,
    subtitle: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    val isSelected = selected == title
    OutlinedCard(
        onClick = { onSelect(title) },
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ModalityPreview() {
    MaterialTheme {
        ModalityScreen(selected = "MEDIO", onSelect = {}, onContinue = {}, onOpenHistory = {})
    }
}
