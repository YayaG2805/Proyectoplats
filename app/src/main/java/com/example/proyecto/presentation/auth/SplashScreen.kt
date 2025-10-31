package com.example.proyecto.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.proyecto.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) { delay(1200); onFinish() }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.piggy_logo),
                contentDescription = "PiggyMobile logo",
                modifier = Modifier.size(140.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text("PiggyMobile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(20.dp))
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashPreview() {
    MaterialTheme { SplashScreen(onFinish = {}) }
}
