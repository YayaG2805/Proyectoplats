package com.example.proyecto.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

data class HistoryRow(val mes: String, val ingreso: String, val ahorro: String, val estado: String)

@Composable
fun HistoryScreen(rows: List<HistoryRow>, onAddNew: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mi historial de ahorro", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        if (rows.isEmpty()) {
            Text("Aún no tienes registros de ahorro.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
        } else {
            // aquí dejas todo lo que ya tenías: tabs Mensual/Semanal/Todos, tabla, etc.
            // HeaderRow()
            // rows.forEach { RowItem(it) }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = onAddNew,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Agregar nuevo registro") }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { /* comparar resultados */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = rows.size >= 2   // por ejemplo, solo si hay al menos 2 registros
        ) {
            Text("Comparar resultados")
        }
    }
}


@Composable private fun HeaderRow() {
    Row(Modifier.fillMaxWidth()) {
        Text("Mes", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Text("Ingreso", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Text("Ahorro", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Text("Estado", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
    }
}

@Composable private fun RowItem(r: HistoryRow) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(r.mes, Modifier.weight(1f))
        Text(r.ingreso, Modifier.weight(1f))
        Text(r.ahorro, Modifier.weight(1f))
        Text(r.estado, Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryPreview() {
    MaterialTheme {
        HistoryScreen(
            rows = listOf(
                HistoryRow("Marzo", "Q 7000", "Q 700", "Cumplido"),
                HistoryRow("Febrero", "Q 7000", "Q 700", "Parcial"),
                HistoryRow("Enero", "Q 7000", "Q 700", "No cumplido"),
            ),
            onAddNew = {}
        )
    }
}
