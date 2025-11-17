package com.example.proyecto.presentation.flow

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyecto.domain.model.BudgetData
import kotlin.math.roundToInt


@Composable
fun TipsScreen(data: BudgetData, onBackToSummary: ()->Unit) {
    val tips = generatePersonalizedTips(data)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Consejos personalizados para ti", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Basado en tu modalidad: ${data.modality}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))

        tips.forEach { tip ->
            TipCard(tip)
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onBackToSummary, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al resumen")
        }
    }
}

@Composable
private fun TipCard(tip: PersonalizedTip) {
    ElevatedCard(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = when(tip.priority) {
                TipPriority.HIGH -> MaterialTheme.colorScheme.errorContainer
                TipPriority.MEDIUM -> MaterialTheme.colorScheme.primaryContainer
                TipPriority.LOW -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    tip.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (tip.potentialSaving > 0) {
                    Text(
                        "Ahorro: Q${tip.potentialSaving.roundToInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(tip.message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

data class PersonalizedTip(
    val category: String,
    val message: String,
    val potentialSaving: Double = 0.0,
    val priority: TipPriority = TipPriority.MEDIUM
)

enum class TipPriority { HIGH, MEDIUM, LOW }

fun generatePersonalizedTips(data: BudgetData): List<PersonalizedTip> {
    val tips = mutableListOf<PersonalizedTip>()
    val totalExpenses = data.rent + data.utilities + data.transport + data.other
    val balance = data.income - totalExpenses
    val savingPercentage = if (data.income > 0) (balance / data.income) * 100 else 0.0

    // Tips según modalidad
    when (data.modality) {
        "EXTREMO" -> {
            tips.add(PersonalizedTip(
                "MODO EXTREMO",
                "Estás en modo extremo. Considera el reto '30 días sin gastos innecesarios' para maximizar tu ahorro.",
                priority = TipPriority.HIGH
            ))

            if (data.other > 0) {
                tips.add(PersonalizedTip(
                    "Otros gastos",
                    "Tienes Q${data.other.roundToInt()} en otros gastos. En modo extremo, intenta reducirlos al 100% este mes.",
                    potentialSaving = data.other,
                    priority = TipPriority.HIGH
                ))
            }

            tips.add(PersonalizedTip(
                "Transporte",
                "Considera caminar o usar bicicleta 3 días a la semana. Ahorro potencial: 50% de Q${data.transport.roundToInt()}",
                potentialSaving = data.transport * 0.5,
                priority = TipPriority.MEDIUM
            ))
        }

        "MEDIO" -> {
            tips.add(PersonalizedTip(
                "MODO EQUILIBRADO",
                "Balance perfecto entre ahorro y calidad de vida. Mantén el enfoque en gastos conscientes.",
                priority = TipPriority.MEDIUM
            ))

            if (data.other > data.income * 0.15) {
                tips.add(PersonalizedTip(
                    "Otros gastos",
                    "Tus 'otros gastos' superan el 15% de tu ingreso. Reduce a Q${(data.income * 0.10).roundToInt()} para mejorar.",
                    potentialSaving = data.other - (data.income * 0.10),
                    priority = TipPriority.MEDIUM
                ))
            }

            tips.add(PersonalizedTip(
                "Streaming y suscripciones",
                "Revisa tus suscripciones. Cancela 1-2 que uses menos. Ahorro estimado: Q80-Q150/mes",
                potentialSaving = 115.0,
                priority = TipPriority.LOW
            ))
        }

        "IMPREVISTO" -> {
            val emergencyFund = data.income * 0.20
            tips.add(PersonalizedTip(
                "FONDO DE EMERGENCIA",
                "Tu objetivo: acumular Q${emergencyFund.roundToInt()} (20% de tu ingreso) para imprevistos.",
                priority = TipPriority.HIGH
            ))

            if (savingPercentage < 15) {
                tips.add(PersonalizedTip(
                    "Meta de ahorro",
                    "Actualmente ahorras ${savingPercentage.roundToInt()}%. Para imprevistos, apunta al 15-20% de tu ingreso.",
                    potentialSaving = (data.income * 0.15) - balance,
                    priority = TipPriority.HIGH
                ))
            }
        }
    }

    // Tips generales basados en gastos específicos
    if (data.transport > data.income * 0.15) {
        tips.add(PersonalizedTip(
            "Transporte",
            "Tu transporte es ${((data.transport/data.income)*100).roundToInt()}% de tu ingreso. Ideal: 10-15%. Prueba compartir auto o transporte público.",
            potentialSaving = data.transport - (data.income * 0.12),
            priority = TipPriority.MEDIUM
        ))
    }

    if (data.utilities > data.income * 0.10) {
        tips.add(PersonalizedTip(
            "Servicios",
            "Servicios altos (${((data.utilities/data.income)*100).roundToInt()}%). Desconecta aparatos no usados, reduce agua caliente.",
            potentialSaving = data.utilities * 0.15,
            priority = TipPriority.LOW
        ))
    }

    // Tips de ahorro general
    tips.add(PersonalizedTip(
        "Compras inteligentes",
        "Compra a granel: arroz, frijol, pasta. Ahorro promedio: 8-12% vs compras semanales pequeñas.",
        potentialSaving = totalExpenses * 0.10,
        priority = TipPriority.LOW
    ))

    if (balance < data.income * 0.10) {
        tips.add(PersonalizedTip(
            "ALERTA",
            "Tu ahorro actual es muy bajo. Revisa gastos hormiga: café, antojos, apps de delivery.",
            priority = TipPriority.HIGH
        ))
    }

    return tips.sortedByDescending { it.priority }
}

@Preview(showBackground = true)
@Composable
private fun TipsPreview() {
    MaterialTheme {
        TipsScreen(
            data = BudgetData(
                income = 7000.0,
                rent = 2000.0,
                utilities = 500.0,
                transport = 800.0,
                other = 600.0,
                modality = "MEDIO"
            ),
            onBackToSummary = {}
        )
    }
}