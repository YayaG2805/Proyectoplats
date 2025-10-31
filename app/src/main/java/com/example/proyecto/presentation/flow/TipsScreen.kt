package com.example.proyecto.presentation.flow

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun TipsScreen(onBackToSummary: ()->Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Consejos para mejorar tu ahorro", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        TipCard("Reduce streaming a 1 servicio este mes: ahorras Q80–Q120.")
        TipCard("Cocina en casa 3 días: ahorras ~Q200.")
        TipCard("Transporte público 2 días: ~Q100 extra.")
        TipCard("Compra a granel y planifica menú: -8% en supermercados.")
        Spacer(Modifier.height(12.dp))
        Button(onClick = onBackToSummary, modifier = Modifier.fillMaxWidth()) { Text("Volver al resumen") }
    }
}

@Composable private fun TipCard(text: String) {
    ElevatedCard(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text, modifier = Modifier.padding(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun TipsPreview() {
    MaterialTheme { TipsScreen(onBackToSummary = {}) }
}

