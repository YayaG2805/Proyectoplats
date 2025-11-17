package com.example.proyecto.presentation.savingsindex

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsIndexScreen(
    onBack: () -> Unit,
    vm: SavingsIndexViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Índice de Ahorro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (!uiState.hasData) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "No hay datos suficientes",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Necesitas al menos un presupuesto mensual para ver tu índice de ahorro",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Resumen general
                Text(
                    "Resumen General",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Índice de Ahorro", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    "${uiState.savingPercentage.toInt()}%",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Estado", style = MaterialTheme.typography.labelMedium)
                                Text(
                                    uiState.savingStatus,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = when (uiState.savingStatus) {
                                        "Excelente" -> Color(0xFF4CAF50)
                                        "Bueno" -> Color(0xFF2196F3)
                                        "Regular" -> Color(0xFFFF9800)
                                        else -> Color(0xFFF44336)
                                    }
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { (uiState.savingPercentage / 100).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth(),
                            color = when {
                                uiState.savingPercentage >= 30 -> Color(0xFF4CAF50)
                                uiState.savingPercentage >= 20 -> Color(0xFF2196F3)
                                uiState.savingPercentage >= 10 -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            },
                        )
                    }
                }

                // Gráfica de dona - Distribución de gastos
                Text(
                    "Distribución de Gastos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                DonutChart(
                    data = listOf(
                        ChartData("Ahorro", uiState.totalSavings, Color(0xFF4CAF50)),
                        ChartData("Renta", uiState.totalRent, Color(0xFF2196F3)),
                        ChartData("Servicios", uiState.totalUtilities, Color(0xFFFF9800)),
                        ChartData("Transporte", uiState.totalTransport, Color(0xFF9C27B0)),
                        ChartData("Otros", uiState.totalOther, Color(0xFFF44336))
                    )
                )

                // Estadísticas detalladas
                Text(
                    "Estadísticas Detalladas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatRow("Ingreso Total", uiState.totalIncome.q(), Icons.Default.AccountBalance)
                        Divider()
                        StatRow("Total Ahorrado", uiState.totalSavings.q(), Icons.Default.Savings, highlight = true)
                        StatRow("Gasto en Renta", uiState.totalRent.q(), Icons.Default.Home)
                        StatRow("Gasto en Servicios", uiState.totalUtilities.q(), Icons.Default.ElectricBolt)
                        StatRow("Gasto en Transporte", uiState.totalTransport.q(), Icons.Default.DirectionsCar)
                        StatRow("Otros Gastos", uiState.totalOther.q(), Icons.Default.ShoppingCart)
                        Divider()
                        StatRow("Total Gastado", uiState.totalExpenses.q(), Icons.Default.TrendingDown)
                    }
                }

                // Tendencia mensual
                if (uiState.monthlyTrend.size > 1) {
                    Text(
                        "Tendencia Mensual",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.monthlyTrend.forEach { trend ->
                                MonthTrendRow(trend)
                            }
                        }
                    }
                }

                // Consejos
                Text(
                    "Recomendaciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                uiState.recommendations.forEach { rec ->
                    RecommendationCard(rec)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DonutChart(data: List<ChartData>) {
    val total = data.sumOf { it.value }
    if (total <= 0) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasSize = size.minDimension
                val radius = canvasSize / 2
                val strokeWidth = canvasSize * 0.25f
                val centerOffset = Offset(size.width / 2, size.height / 2)

                var startAngle = -90f

                data.forEach { item ->
                    val sweepAngle = (item.value / total * 360).toFloat()

                    drawArc(
                        color = item.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(
                            centerOffset.x - radius,
                            centerOffset.y - radius
                        ),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )

                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    total.q(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text("Total", style = MaterialTheme.typography.bodySmall)
            }
        }

        // Leyenda
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(16.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(color = item.color)
                            }
                        }
                        Text(item.label, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        "${((item.value / total) * 100).toInt()}% - ${item.value.q()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            value,
            style = if (highlight) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyMedium,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MonthTrendRow(trend: MonthTrend) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(trend.month, style = MaterialTheme.typography.bodyMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${trend.percentage.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    trend.percentage >= 20 -> Color(0xFF4CAF50)
                    trend.percentage >= 10 -> Color(0xFF2196F3)
                    else -> Color(0xFFFF9800)
                }
            )
            Icon(
                if (trend.trend == "up") Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (trend.trend == "up") Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}

@Composable
private fun RecommendationCard(recommendation: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                recommendation,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

data class ChartData(
    val label: String,
    val value: Double,
    val color: Color
)

data class MonthTrend(
    val month: String,
    val percentage: Double,
    val trend: String // "up" or "down"
)

private fun Double.q(): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "GT"))
    return nf.format(this)
}