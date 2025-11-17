package com.example.proyecto.presentation.test

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto.notifications.NotificationHelper

/**
 * Pantalla de prueba para verificar que las notificaciones funcionan correctamente.
 *
 * USO:
 * 1. Navegar a esta pantalla desde el perfil o agregar un botón de acceso
 * 2. Presionar "Enviar notificación de prueba"
 * 3. La notificación debe aparecer en la barra de notificaciones del dispositivo
 * 4. Al tocar la notificación, debe abrir la app en la pantalla de gastos diarios
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestNotificationScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val notificationHelper = NotificationHelper(context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prueba de Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                "Prueba de Notificaciones",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Usa estos botones para verificar que las notificaciones funcionan correctamente en tu dispositivo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "📱 Notificación con gastos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Simula que ya has registrado gastos hoy",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        onClick = {
                            notificationHelper.sendDailyReminder(hasExpensesToday = true)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enviar notificación")
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "📝 Notificación sin gastos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Simula que no has registrado gastos hoy",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        onClick = {
                            notificationHelper.sendDailyReminder(hasExpensesToday = false)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enviar notificación")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "ℹ️ Información",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "• Las notificaciones reales se envían diariamente a las 8:00 PM",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "• Al tocar una notificación, la app se abre en Gastos Diarios",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "• Asegúrate de tener los permisos de notificaciones activados",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "• En Android 13+, la app pedirá permiso la primera vez",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                "Verifica que la notificación aparezca en tu barra de notificaciones",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}