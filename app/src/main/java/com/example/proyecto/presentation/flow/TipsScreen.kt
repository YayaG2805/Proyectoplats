package com.example.proyecto.presentation.flow

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto.domain.model.BudgetData
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun TipsScreen(
    data: BudgetData,
    onBackToSummary: () -> Unit,
    vm: TipsViewModel = koinViewModel()
) {
    // Obtener gastos por categoría desde ViewModel
    val categoryExpenses by vm.categoryExpenses.collectAsState()

    val tips = generatePersonalizedTips(data, categoryExpenses)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Consejos personalizados para ti", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Basado en tu modalidad: ${data.modality}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Y tus gastos reales del mes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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

// Data class para gastos por categoría (usado desde ViewModel)
data class CategoryExpenseData(
    val category: String,
    val total: Double,
    val count: Int
)

fun generatePersonalizedTips(
    data: BudgetData,
    categoryExpenses: List<CategoryExpenseData>
): List<PersonalizedTip> {
    val tips = mutableListOf<PersonalizedTip>()
    val totalExpenses = data.rent + data.utilities + data.transport + data.other
    val balance = data.income - totalExpenses
    val savingPercentage = if (data.income > 0) (balance / data.income) * 100 else 0.0

    // TIPS BASADOS EN GASTOS DIARIOS REALES POR CATEGORÍA
    val totalDailyExpenses = categoryExpenses.sumOf { it.total }

    // 1. Tips por categoría de gasto diario
    categoryExpenses.sortedByDescending { it.total }.take(3).forEach { expense ->
        val percentage = if (totalDailyExpenses > 0) (expense.total / totalDailyExpenses) * 100 else 0.0

        when (expense.category) {
            "COMIDA" -> {
                if (percentage > 40) {
                    tips.add(PersonalizedTip(
                        "COMIDA - Tu mayor gasto",
                        "Gastas Q${expense.total.roundToInt()} en comida (${percentage.roundToInt()}% de tus gastos variables). " +
                                "Considera: cocinar más en casa, comprar en mercados locales, evitar delivery diario.",
                        potentialSaving = expense.total * 0.30,
                        priority = TipPriority.HIGH
                    ))
                } else if (percentage > 25) {
                    tips.add(PersonalizedTip(
                        "COMIDA",
                        "Tu gasto en comida es Q${expense.total.roundToInt()}. Tip: prepara comidas para toda la semana (meal prep) " +
                                "y lleva lunch al trabajo/universidad. Ahorro estimado: 25-30%",
                        potentialSaving = expense.total * 0.25,
                        priority = TipPriority.MEDIUM
                    ))
                }
            }

            "TRANSPORTE" -> {
                if (percentage > 30) {
                    tips.add(PersonalizedTip(
                        "TRANSPORTE - Gasto elevado",
                        "Gastas Q${expense.total.roundToInt()} en transporte. Alternativas: usa transporte público, " +
                                "comparte viajes (carpooling), considera bicicleta para distancias cortas.",
                        potentialSaving = expense.total * 0.40,
                        priority = TipPriority.HIGH
                    ))
                } else if (percentage > 20) {
                    tips.add(PersonalizedTip(
                        "TRANSPORTE",
                        "Transporte: Q${expense.total.roundToInt()}. Planifica rutas eficientes y agrupa tus salidas " +
                                "para hacer varias cosas en un solo viaje.",
                        potentialSaving = expense.total * 0.20,
                        priority = TipPriority.MEDIUM
                    ))
                }
            }

            "ENTRETENIMIENTO" -> {
                if (percentage > 25) {
                    tips.add(PersonalizedTip(
                        "ENTRETENIMIENTO - Alto",
                        "Gastas Q${expense.total.roundToInt()} en entretenimiento. Alternativas gratis: eventos públicos, " +
                                "parques, actividades al aire libre, noches de juegos en casa.",
                        potentialSaving = expense.total * 0.50,
                        priority = TipPriority.HIGH
                    ))
                } else if (percentage > 15) {
                    tips.add(PersonalizedTip(
                        "ENTRETENIMIENTO",
                        "Entretenimiento: Q${expense.total.roundToInt()}. Reduce salidas a 1-2 por mes, " +
                                "cancela suscripciones que no uses, busca opciones gratuitas.",
                        potentialSaving = expense.total * 0.30,
                        priority = TipPriority.MEDIUM
                    ))
                }
            }

            "SERVICIOS" -> {
                if (percentage > 20) {
                    tips.add(PersonalizedTip(
                        "SERVICIOS",
                        "Gastas Q${expense.total.roundToInt()} en servicios extra. Revisa suscripciones (streaming, apps, gym) " +
                                "y cancela las que no uses. Ahorro típico: Q80-150/mes",
                        potentialSaving = 115.0,
                        priority = TipPriority.MEDIUM
                    ))
                }
            }

            "SALUD" -> {
                tips.add(PersonalizedTip(
                    "SALUD",
                    "Gastos en salud: Q${expense.total.roundToInt()}. Importante no descuidar esto, " +
                            "pero considera: usar genéricos, chequeos preventivos gratuitos, y seguro médico.",
                    priority = TipPriority.LOW
                ))
            }

            "OTROS" -> {
                if (percentage > 30) {
                    tips.add(PersonalizedTip(
                        "OTROS - Gastos hormiga detectados",
                        "Q${expense.total.roundToInt()} en gastos no categorizados. ALERTA: Son los gastos hormiga. " +
                                "Registra TODO por una semana para identificar dónde se va tu dinero.",
                        potentialSaving = expense.total * 0.40,
                        priority = TipPriority.HIGH
                    ))
                }
            }
        }
    }

    // 2. Tips según modalidad
    when (data.modality) {
        "EXTREMO" -> {
            tips.add(PersonalizedTip(
                "MODO EXTREMO",
                "Estás en modo extremo. Meta: ahorrar 30%+ de tu ingreso. " +
                        "Reto: '30 días sin gastos innecesarios'. Solo gastos esenciales este mes.",
                priority = TipPriority.HIGH
            ))

            if (totalDailyExpenses > data.income * 0.20) {
                tips.add(PersonalizedTip(
                    "ALERTA MODO EXTREMO",
                    "Has gastado Q${totalDailyExpenses.roundToInt()} en gastos variables. " +
                            "En modo extremo deberías mantenerlos bajo Q${(data.income * 0.15).roundToInt()}",
                    priority = TipPriority.HIGH
                ))
            }
        }

        "MEDIO" -> {
            tips.add(PersonalizedTip(
                "MODO EQUILIBRADO",
                "Balance entre ahorro y calidad de vida. Meta: ahorrar 15% de tu ingreso. " +
                        "Mantén gastos conscientes sin sacrificar todo.",
                priority = TipPriority.MEDIUM
            ))

            if (savingPercentage < 10) {
                tips.add(PersonalizedTip(
                    "Mejorar ahorro",
                    "Actualmente ahorras ${savingPercentage.roundToInt()}%. Para modo medio, " +
                            "intenta llegar al 15%. Necesitas reducir gastos en Q${((data.income * 0.15) - balance).roundToInt()}",
                    potentialSaving = (data.income * 0.15) - balance,
                    priority = TipPriority.MEDIUM
                ))
            }
        }

        "IMPREVISTO" -> {
            val emergencyFund = data.income * 0.20
            tips.add(PersonalizedTip(
                "FONDO DE EMERGENCIA",
                "Meta: acumular Q${emergencyFund.roundToInt()} (20% de tu ingreso) para imprevistos. " +
                        "Este mes intenta ahorrar Q${(emergencyFund / 3).roundToInt()} mínimo.",
                priority = TipPriority.HIGH
            ))
        }
    }

    // 3. Tips sobre gastos fijos del presupuesto
    if (data.rent > data.income * 0.30) {
        tips.add(PersonalizedTip(
            "Renta alta",
            "Tu renta es ${((data.rent/data.income)*100).roundToInt()}% de tu ingreso. " +
                    "Ideal: 25-30%. Considera mudarte o buscar compañero de casa.",
            potentialSaving = data.rent - (data.income * 0.25),
            priority = TipPriority.MEDIUM
        ))
    }

    if (data.transport > data.income * 0.15) {
        tips.add(PersonalizedTip(
            "Transporte fijo alto",
            "Transporte fijo: Q${data.transport.roundToInt()}. Considera opciones más económicas: " +
                    "transporte público, bicicleta, vivir más cerca del trabajo/estudio.",
            potentialSaving = data.transport * 0.30,
            priority = TipPriority.LOW
        ))
    }

    // 4. Tips generales de ahorro
    if (balance < data.income * 0.10) {
        tips.add(PersonalizedTip(
            "ALERTA - Ahorro bajo",
            "Tu ahorro actual es muy bajo (${savingPercentage.roundToInt()}%). " +
                    "Aplica la regla 50/30/20: 50% necesidades, 30% gustos, 20% ahorro.",
            priority = TipPriority.HIGH
        ))
    }

    if (tips.size < 3) {
        tips.add(PersonalizedTip(
            "Consejo general",
            "Regla de oro: antes de comprar algo, espera 24 horas. Si después de un día todavía lo quieres, cómpralo. " +
                    "Esto elimina compras impulsivas.",
            priority = TipPriority.LOW
        ))
    }

    // Ordenar por prioridad
    return tips.sortedWith(compareBy<PersonalizedTip> {
        when(it.priority) {
            TipPriority.HIGH -> 0
            TipPriority.MEDIUM -> 1
            TipPriority.LOW -> 2
        }
    }.thenByDescending { it.potentialSaving })
}