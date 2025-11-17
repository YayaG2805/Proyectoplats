package com.example.proyecto.presentation.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.proyecto.presentation.navigation.DailyExpenseDest
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryRoute(
    onAddNew: () -> Unit,
    navController: NavController? = null
) {
    val vm: HistoryViewModel = koinViewModel()
    val rows = vm.rows.collectAsState().value

    HistoryScreen(
        rows = rows,
        onAddNew = onAddNew,
        onOpenDailyExpense = {
            navController?.navigate(DailyExpenseDest)
        },
        onDelete = { budgetId ->
            vm.deleteById(budgetId)
        }
    )
}