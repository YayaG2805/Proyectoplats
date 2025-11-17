package com.example.proyecto.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyecto.notifications.NotificationHelper

data class HistoryRow(
    val id: Long = 0,
    val mes: String,
    val ingreso: String,
    val ahorro: String,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    rows: List<HistoryRow>,
    onAddNew: () -> Unit,
    onOpenDailyExpense: () -> Unit,
    onDelete: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val (showDeleteDialog, setShowDeleteDialog) = remember { mutableStateOf<Long?>(null) }

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
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aún no tienes registros de ahorro.\n¡Crea tu primer presupuesto mensual!",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                HeaderRow()
                Spacer(Modifier.height(4.dp))
                rows.forEach { row ->
                    RowItem(row, onDeleteClick = { setShowDeleteDialog(row.id) })
                }
                Spacer(Modifier.weight(1f))
            }

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

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { setShowDeleteDialog(null) },
            icon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("¿Eliminar registro?") },
            text = { Text("Esta acción no se puede deshacer. ¿Estás seguro?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(showDeleteDialog)
                        setShowDeleteDialog(null)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowDeleteDialog(null) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun HeaderRow() {
    Row(Modifier.fillMaxWidth()) {
        Text("Mes", Modifier.weight(1.2f), style = MaterialTheme.typography.labelLarge)
        Text("Ingreso", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Text("Ahorro", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Text("Estado", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(40.dp)) // Espacio para el botón de eliminar
    }
}

@Composable
private fun RowItem(r: HistoryRow, onDeleteClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(r.mes, Modifier.weight(1.2f), style = MaterialTheme.typography.bodySmall)
        Text(r.ingreso, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text(r.ahorro, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)

        // Estado con color
        val estadoColor = when (r.estado) {
            "Cumplido" -> MaterialTheme.colorScheme.primary
            "Parcial" -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.error
        }
        Text(
            r.estado,
            Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = estadoColor
        )

        // Botón de eliminar
        IconButton(onClick = onDeleteClick, modifier = Modifier.size(40.dp)) {
            Icon(
                Icons.Default.Delete,
                "Eliminar",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryPreview() {
    MaterialTheme {
        HistoryScreen(
            rows = listOf(
                HistoryRow(1, "Marzo 2025", "Q 7000", "Q 700", "Cumplido"),
                HistoryRow(2, "Febrero 2025", "Q 7000", "Q 500", "Parcial"),
                HistoryRow(3, "Enero 2025", "Q 7000", "Q 300", "No cumplido"),
            ),
            onAddNew = {},
            onOpenDailyExpense = {},
            onDelete = {}
        )
    }
}