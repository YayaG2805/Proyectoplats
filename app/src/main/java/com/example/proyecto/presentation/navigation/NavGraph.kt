package com.example.proyecto.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import com.example.proyecto.presentation.auth.AuthRoute
import com.example.proyecto.presentation.auth.SplashScreen
import com.example.proyecto.presentation.dailyexpense.DailyExpenseRoute
import com.example.proyecto.presentation.flow.BudgetFlowViewModel
import com.example.proyecto.presentation.flow.TipsBottomNavRoute
import com.example.proyecto.presentation.history.HistoryRoute
import com.example.proyecto.presentation.home.HomeScreen
import com.example.proyecto.presentation.newprofile.NewProfileScreen
import com.example.proyecto.presentation.detail.DetailScreen
import com.example.proyecto.presentation.profile.ProfileScreen
import com.example.proyecto.presentation.history.HistoryViewModel
import com.example.proyecto.presentation.newprofile.NewProfileViewModel
import com.example.proyecto.presentation.savingsindex.SavingsIndexScreen

// ===== RUTAS DE NAVEGACIÓN =====

@Serializable object SplashDest
@Serializable object AuthDest
@Serializable object TipsDest
@Serializable object HistoryDest
@Serializable object DailyExpenseDest
@Serializable object ProfileDest
@Serializable object HomeDest
@Serializable object SavingsIndexDest
@Serializable data class NewProfileDest(val redirectToId: Long)
@Serializable data class DetailDest(val id: Long)

/**
 * NavHost principal de PiggyMobile.
 *
 * CAMBIOS PRINCIPALES:
 * - Un solo presupuesto mensual activo
 * - Editar/agregar ingreso al presupuesto actual
 * - Nueva ruta: SavingsIndexDest (reemplaza comparar resultados)
 * - Tips personalizados por categoría de gasto
 */
@Composable
fun AppNavHost(startWithDailyExpense: Boolean = false) {
    val nav = rememberNavController()
    val budgetVM: BudgetFlowViewModel = koinViewModel()
    val historyVM: HistoryViewModel = koinViewModel()

    // Obtener la ruta actual para el Bottom Bar
    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Verificar si hay presupuestos guardados para mostrar Tips
    val hasMonthlyBudget by historyVM.rows.collectAsState()
    val showTipsTab = hasMonthlyBudget.isNotEmpty()

    // Determinar si mostrar Bottom Bar
    val shouldShowBottomBar = when {
        currentRoute?.contains("HistoryDest") == true -> true
        currentRoute?.contains("DailyExpenseDest") == true -> true
        currentRoute?.contains("TipsDest") == true -> true
        currentRoute?.contains("ProfileDest") == true -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                PiggyBottomBar(
                    currentRoute = when {
                        currentRoute?.contains("HistoryDest") == true -> HistoryDest
                        currentRoute?.contains("DailyExpenseDest") == true -> DailyExpenseDest
                        currentRoute?.contains("TipsDest") == true -> TipsDest
                        currentRoute?.contains("ProfileDest") == true -> ProfileDest
                        else -> null
                    },
                    onNavigate = { destination ->
                        nav.navigate(destination) {
                            popUpTo(HistoryDest) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    hasMonthlyBudget = showTipsTab
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = if (startWithDailyExpense) DailyExpenseDest else SplashDest,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable<SplashDest> {
                SplashScreen(
                    onFinish = {
                        nav.navigate(AuthDest) {
                            popUpTo(SplashDest) { inclusive = true }
                        }
                    }
                )
            }

            composable<AuthDest> {
                AuthRoute(
                    onLoggedIn = {
                        val destination = if (startWithDailyExpense) DailyExpenseDest else HistoryDest
                        nav.navigate(destination) {
                            popUpTo(AuthDest) { inclusive = true }
                        }
                    },
                    onRegistered = {
                        nav.navigate(HistoryDest) {
                            popUpTo(AuthDest) { inclusive = true }
                        }
                    }
                )
            }

            // Tips - muestra del último presupuesto guardado
            composable<TipsDest> {
                TipsBottomNavRoute()
            }

            // ===== PANTALLAS DEL BOTTOM NAV =====

            composable<HistoryDest> {
                HistoryRoute(
                    onEditCurrent = {
                        nav.navigate(NewProfileDest(redirectToId = 0L))
                    },
                    onViewSavingsIndex = {
                        nav.navigate(SavingsIndexDest)
                    },
                    navController = nav
                )
            }

            composable<DailyExpenseDest> {
                DailyExpenseRoute(
                    onBack = {
                        if (startWithDailyExpense) {
                            nav.navigate(HistoryDest) {
                                popUpTo(DailyExpenseDest) { inclusive = true }
                            }
                        } else {
                            nav.popBackStack()
                        }
                    }
                )
            }

            composable<ProfileDest> {
                ProfileScreen(
                    onLogout = {
                        nav.navigate(AuthDest) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // ===== OTRAS RUTAS =====

            composable<HomeDest> {
                HomeScreen(
                    onNew = { nav.navigate(NewProfileDest(redirectToId = 0L)) },
                    onOpenDetail = { id -> nav.navigate(DetailDest(id = id)) }
                )
            }

            composable<NewProfileDest> { backStackEntry ->
                val args = backStackEntry.toRoute<NewProfileDest>()
                val vm: NewProfileViewModel = koinViewModel()

                NewProfileScreen(
                    onSaved = { newId ->
                        nav.navigate(HistoryDest) {
                            popUpTo(HistoryDest) { inclusive = false }
                        }
                    },
                    onBack = { nav.popBackStack() }
                )
            }

            composable<DetailDest> { backStackEntry ->
                val args = backStackEntry.toRoute<DetailDest>()
                DetailScreen(
                    profileId = args.id,
                    onBack = { nav.popBackStack() }
                )
            }

            // NUEVA RUTA: Índice de Ahorro
            composable<SavingsIndexDest> {
                SavingsIndexScreen(
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}