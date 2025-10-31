package com.example.proyecto.presentation.auth

import androidx.compose.runtime.Composable

@Composable
fun AuthRoute(
    onLoggedIn: () -> Unit,
    onRegistered: () -> Unit
) {
    AuthScreen(onLoggedIn = onLoggedIn, onRegistered = onRegistered)
}
