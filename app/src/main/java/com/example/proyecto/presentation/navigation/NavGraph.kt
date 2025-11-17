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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.proyecto.presentation.history.HistoryViewModel
import org.koin.androidx.compose.koinViewModel


@Serializable object SplashDest
@Serializable object AuthDest
@Serializable object ModalityDest
@Serializable object BudgetFormDest
@Serializable data class SummaryDest(val profileId: Long)
@Serializable object TipsDest
@Serializable object HistoryDest
@Serializable object DailyExpenseDest
@Serializable object HomeDest
@Serializable data class NewProfileDest(val redirectToId: Long)
@Serializable data class DetailDest(val id: Long)

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val budgetVM: BudgetFlowViewModel = koinViewModel()
    val historyVM: HistoryViewModel = koinViewModel()
    var selectedModality by rememberSaveable { mutableStateOf("MEDIO") }

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
                    nav.navigate(HistoryDest) {
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
                        nav.navigate(NewProfileDest(redirectToId = args.profileId))
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

        composable<HistoryDest> {
            HistoryRoute(
                onAddNew = { nav.navigate(NewProfileDest(redirectToId = 0L)) },
                navController = nav
            )
        }

        composable<DailyExpenseDest> {
            DailyExpenseRoute(
                onBack = { nav.popBackStack() }
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
                onBack = { nav.popBackStack() },
                onChangeModality = {
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