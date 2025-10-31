package com.example.proyecto.presentation.flow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto.domain.model.BudgetData
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SummaryScreen(
    data: BudgetData,
    onSeeTips: () -> Unit,
    onSaveToHistory: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalExpenses = data.rent + data.utilities + data.transport + data.other
    val balance = data.income - totalExpenses

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Resumen de tu presupuesto",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RowItem("Ingreso mensual", data.income.q())
                RowItem("Renta", data.rent.q())
                RowItem("Servicios", data.utilities.q())
                RowItem("Transporte", data.transport.q())
                RowItem("Otros", data.other.q())

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                RowItem("Gasto total", totalExpenses.q(), bold = true)
                RowItem("Balance", balance.q(), bold = true)

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Modalidad: ${data.modality}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onSeeTips
            ) { Text("Ver tips") }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onSaveToHistory
            ) { Text("Guardar") }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onContinue
            ) { Text("Continuar") }
        }
    }
}

@Composable
private fun RowItem(
    label: String,
    value: String,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = if (bold)
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            else
                MaterialTheme.typography.bodyLarge
        )
    }
}

/** Formatea en Quetzales con locale es_GT. */
private fun Double.q(): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "GT"))
    return nf.format(this)
}
