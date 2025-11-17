package com.example.proyecto.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.proyecto.presentation.flow.BudgetFormRoute
import com.example.proyecto.presentation.flow.BudgetFlowViewModel
import com.example.proyecto.presentation.flow.SummaryRoute
import com.example.proyecto.presentation.flow.TipsRoute
import com.example.proyecto.presentation.history.HistoryRoute
import com.example.proyecto.presentation.flow.ModalityRoute
import com.example.proyecto.presentation.home.HomeScreen
import com.example.proyecto.presentation.newprofile.NewProfileScreen
import com.example.proyecto.presentation.detail.DetailScreen
import com.example.proyecto.presentation.profile.ProfileScreen
import com.example.proyecto.presentation.history.HistoryViewModel
import com.example.proyecto.presentation.newprofile.NewProfileViewModel

// ===== RUTAS DE NAVEGACIÓN =====

@Serializable object SplashDest
@Serializable object AuthDest
@Serializable object ModalityDest
@Serializable object BudgetFormDest
@Serializable data class SummaryDest(val profileId: Long)
@Serializable object TipsDest
@Serializable object HistoryDest
@Serializable object DailyExpenseDest
@Serializable object ProfileDest
@Serializable object HomeDest
@Serializable data class NewProfileDest(val redirectToId: Long)
@Serializable data class DetailDest(val id: Long)

/**
 * NavHost principal de PiggyMobile.
 *
 * Maneja toda la navegación de la app, incluyendo:
 * - Flujo de autenticación (Splash → Auth → Modalidad → Formulario → Resumen)
 * - Bottom Navigation (Historial, Gastos Diarios, Perfil)
 * - Deep linking desde notificaciones
 *
 * @param startWithDailyExpense Si true, inicia directamente en DailyExpense (desde notificación)
 */
@Composable
fun AppNavHost(startWithDailyExpense: Boolean = false) {
    val nav = rememberNavController()
    val budgetVM: BudgetFlowViewModel = koinViewModel()
    val historyVM: HistoryViewModel = koinViewModel()
    var selectedModality by rememberSaveable { mutableStateOf("MEDIO") }

    // Obtener la ruta actual para el Bottom Bar
    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determinar si mostrar Bottom Bar (solo en las 3 pantallas principales)
    val shouldShowBottomBar = when {
        currentRoute?.contains("HistoryDest") == true -> true
        currentRoute?.contains("DailyExpenseDest") == true -> true
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
                        currentRoute?.contains("ProfileDest") == true -> ProfileDest
                        else -> null
                    },
                    onNavigate = { destination ->
                        nav.navigate(destination) {
                            // Evitar múltiples copias de la misma pantalla
                            popUpTo(HistoryDest) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
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
                        nav.navigate(ModalityDest) {
                            popUpTo(AuthDest) { inclusive = true }
                        }
                    }
                )
            }

            composable<ModalityDest> {
                ModalityRoute(
                    selected = selectedModality,
                    onSelectedChange = { selectedModality = it },
                    onContinue = {
                        nav.navigate(BudgetFormDest)
                    },
                    onOpenHistory = {
                        nav.navigate(HistoryDest)
                    }
                )
            }

            composable<BudgetFormDest> {
                BudgetFormRoute(
                    initialModality = selectedModality,
                    onSubmit = { data ->
                        budgetVM.set(data)
                        nav.navigate(SummaryDest(profileId = -1L))
                    }
                )
            }

            composable<SummaryDest> { backStackEntry ->
                val args = backStackEntry.toRoute<SummaryDest>()
                val data = budgetVM.pending.value

                if (data == null) {
                    LaunchedEffect(Unit) {
                        val popped = nav.popBackStack(BudgetFormDest, inclusive = false)
                        if (!popped) {
                            nav.navigate(ModalityDest) {
                                popUpTo(HomeDest) { inclusive = false }
                            }
                        }
                    }
                } else {
                    SummaryRoute(
                        data = data,
                        onSeeTips = { nav.navigate(TipsDest) },
                        onSaveToHistory = {
                            historyVM.addFromBudget(data)
                            budgetVM.clear()
                            nav.navigate(HistoryDest) {
                                popUpTo(HistoryDest) { inclusive = false }
                            }
                        },
                        onContinue = {
                            historyVM.addFromBudget(data)
                            budgetVM.clear()
                            nav.navigate(HistoryDest) {
                                popUpTo(HistoryDest) { inclusive = false }
                            }
                        }
                    )
                }
            }

            composable<TipsDest> {
                val data = budgetVM.pending.value
                if (data != null) {
                    TipsRoute(data = data, onBack = { nav.popBackStack() })
                } else {
                    LaunchedEffect(Unit) { nav.popBackStack() }
                }
            }

            // ===== PANTALLAS DEL BOTTOM NAV =====

            composable<HistoryDest> {
                HistoryRoute(
                    onAddNew = { nav.navigate(NewProfileDest(redirectToId = 0L)) },
                    navController = nav
                )
            }

            composable<DailyExpenseDest> {
                DailyExpenseRoute(
                    onBack = {
                        // Si viene de notificación, ir al historial
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

                // Configurar modalidad si viene del flujo
                LaunchedEffect(selectedModality) {
                    vm.setModality(selectedModality)
                }

                NewProfileScreen(
                    onSaved = { newId ->
                        // Ir al historial después de guardar
                        nav.navigate(HistoryDest) {
                            popUpTo(HistoryDest) { inclusive = false }
                        }
                    },
                    onBack = { nav.popBackStack() },
                    onChangeModality = { currentModality ->
                        // Ir a selección de modalidad
                        selectedModality = currentModality
                        nav.navigate(ModalityDest)
                    }
                )
            }

            composable<DetailDest> { backStackEntry ->
                val args = backStackEntry.toRoute<DetailDest>()
                DetailScreen(
                    profileId = args.id,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}