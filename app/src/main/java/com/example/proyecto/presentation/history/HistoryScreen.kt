package com.example.proyecto.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

data class HistoryRow(val mes: String, val ingreso: String, val ahorro: String, val estado: String)

@Composable
fun HistoryScreen(rows: List<HistoryRow>, onAddNew: ()->Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mi historial de ahorro", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        TabRow(selectedTabIndex = 0) { // preview simple
            Tab(selected = true, onClick = {}, text={ Text("Mensual") })
            Tab(selected = false, onClick = {}, text={ Text("Semanal") })
            Tab(selected = false, onClick = {}, text={ Text("Todos") })
        }
        Spacer(Modifier.height(12.dp))
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                HeaderRow()
                Spacer(Modifier.height(6.dp))
                rows.forEach { RowItem(it) }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAddNew, modifier = Modifier.fillMaxWidth()) { Text("Agregar nuevo registro") }
        OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("Comparar resultados") }
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
