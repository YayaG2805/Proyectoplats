package com.example.proyecto.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Datos de cada pestaña del Bottom Navigation Bar.
 */
data class BottomNavItem(
    val route: Any,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * Bottom Navigation Bar de PiggyMobile con 4 pestañas.
 *
 * @param hasMonthlyBudget Si hay al menos un presupuesto mensual guardado
 */
@Composable
fun PiggyBottomBar(
    currentRoute: Any?,
    onNavigate: (Any) -> Unit,
    hasMonthlyBudget: Boolean = false
) {
    val items = buildList {
        add(
            BottomNavItem(
                route = HistoryDest,
                label = "Historial",
                selectedIcon = Icons.Filled.DateRange,
                unselectedIcon = Icons.Outlined.DateRange
            )
        )
        add(
            BottomNavItem(
                route = DailyExpenseDest,
                label = "Gastos",
                selectedIcon = Icons.Filled.ShoppingCart,
                unselectedIcon = Icons.Outlined.ShoppingCart
            )
        )
        // Solo mostrar Tips si hay al menos un registro mensual
        if (hasMonthlyBudget) {
            add(
                BottomNavItem(
                    route = TipsDest,
                    label = "Tips",
                    selectedIcon = Icons.Filled.Lightbulb,
                    unselectedIcon = Icons.Outlined.Lightbulb
                )
            )
        }
        add(
            BottomNavItem(
                route = ProfileDest,
                label = "Perfil",
                selectedIcon = Icons.Filled.AccountCircle,
                unselectedIcon = Icons.Outlined.AccountCircle
            )
        )
    }

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentRoute?.javaClass == item.route.javaClass

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}