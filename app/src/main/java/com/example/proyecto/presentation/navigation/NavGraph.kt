package com.example.proyecto.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

import com.example.proyecto.presentation.auth.AuthRoute
import com.example.proyecto.presentation.auth.SplashScreen
import com.example.proyecto.presentation.flow.BudgetFormRoute
import com.example.proyecto.presentation.flow.BudgetFlowViewModel
import com.example.proyecto.presentation.flow.SummaryRoute
import com.example.proyecto.presentation.flow.TipsRoute
import com.example.proyecto.presentation.history.HistoryRoute
import com.example.proyecto.presentation.flow.ModalityRoute
import com.example.proyecto.presentation.home.HomeScreen
import com.example.proyecto.presentation.newprofile.NewProfileScreen
import com.example.proyecto.presentation.detail.DetailScreen

/* ──────────────────────────────────────────────────────────────
   DESTINOS TYPE-SAFE (@Serializable)
   Mantengo tu patrón y solo añado serialización.
   ────────────────────────────────────────────────────────────── */
@Serializable object SplashDest
@Serializable object AuthDest
@Serializable object ModalityDest
@Serializable object BudgetFormDest
@Serializable data class SummaryDest(val profileId: Long)
@Serializable object TipsDest
@Serializable object HistoryDest
@Serializable object HomeDest
@Serializable data class NewProfileDest(val redirectToId: Long)
@Serializable data class DetailDest(val id: Long)

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val budgetVM: BudgetFlowViewModel = koinViewModel()

    NavHost(navController = nav, startDestination = SplashDest) {

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
                    nav.navigate(ModalityDest) {
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
                onContinue = { nav.navigate(BudgetFormDest) },
                onOpenHistory = { nav.navigate(HistoryDest) }
            )
        }

        composable<BudgetFormDest> {
            // 👇 IMPORTANTE: tu BudgetFormRoute ahora usa onSubmit (no onCalculate).
            BudgetFormRoute(
                onSubmit = { data ->
                    // Guardás el objeto complejo en el ViewModel como ya hacías
                    budgetVM.set(data)
                    // Navegás con un ID primitivo (tu patrón original)
                    nav.navigate(SummaryDest(profileId = -1L))
                }
            )
        }

        composable<SummaryDest> { backStackEntry ->
            val args = backStackEntry.toRoute<SummaryDest>()
            val data = requireNotNull(budgetVM.pending.value) { "BudgetData no disponible" }

            SummaryRoute(
                data = data,
                onSeeTips = { nav.navigate(TipsDest) },
                onSaveToHistory = {
                    // TODO: guarda en Room y obtén newId real
                    val newId = 1L
                    budgetVM.clear()
                    nav.navigate(DetailDest(id = newId)) {
                        popUpTo(BudgetFormDest) { inclusive = true }
                    }
                },
                onContinue = {
                    nav.navigate(NewProfileDest(redirectToId = args.profileId))
                }
            )
        }

        composable<TipsDest> {
            TipsRoute(onBack = { nav.popBackStack() })
        }

        composable<HistoryDest> {
            HistoryRoute(
                onAddNew = { nav.navigate(NewProfileDest(redirectToId = 0L)) }
            )
        }

        composable<HomeDest> {
            HomeScreen(
                onNew = { nav.navigate(NewProfileDest(redirectToId = 0L)) },
                onOpenDetail = { id -> nav.navigate(DetailDest(id = id)) }
            )
        }

        composable<NewProfileDest> { backStackEntry ->
            val args = backStackEntry.toRoute<NewProfileDest>()
            NewProfileScreen(
                onSaved = { newId ->
                    nav.navigate(DetailDest(id = newId)) {
                        popUpTo(HomeDest)
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
    }
}
