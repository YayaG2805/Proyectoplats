package com.example.proyecto.presentation.flow

import androidx.compose.runtime.Composable

@Composable
fun TipsRoute(onBack: ()->Unit) {
    TipsScreen(onBackToSummary = onBack)
}
