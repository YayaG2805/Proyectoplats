package com.example.proyecto.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyecto.notifications.NotificationHelper

data class HistoryRow(val mes: String, val ingreso: String, val ahorro: String, val estado: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    rows: List<HistoryRow>,
    onAddNew: () -> Unit,
    onOpenDailyExpense: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón para PROBAR notificación (debugging)
                SmallFloatingActionButton(
                    onClick = {
                        // Enviar notificación de prueba inmediatamente
                        NotificationHelper(context).sendTestNotification()
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(Icons.Default.Notifications, "Probar notificación")
                }

                // Botón para gastos diarios
                FloatingActionButton(
                    onClick = onOpenDailyExpense,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Add, "Registrar gasto de hoy")
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Mi historial de ahorro", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))

            // Card de acceso rápido
            ElevatedCard(
                onClick = onOpenDailyExpense,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Registro diario de gastos",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Registra lo que gastas hoy 📝",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (rows.isEmpty()) {
                Text("Aún no tienes registros de ahorro.", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
            } else {
                HeaderRow()
                rows.forEach { RowItem(it) }
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = onAddNew,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar nuevo registro mensual") }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { /* comparar resultados */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = rows.size >= 2
            ) {
                Text("Comparar resultados")
            }
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
            onAddNew = {},
            onOpenDailyExpense = {}
        )
    }
}