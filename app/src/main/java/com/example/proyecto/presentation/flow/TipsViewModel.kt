package com.example.proyecto.presentation.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.DailyExpenseDao
import com.example.proyecto.data.local.UserPreferences
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TipsViewModel(
    private val dailyExpenseDao: DailyExpenseDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    private val startOfMonth = "${currentMonth}-01"
    private val endOfMonth = LocalDate.now().withDayOfMonth(
        LocalDate.now().lengthOfMonth()
    ).format(DateTimeFormatter.ISO_LOCAL_DATE)

    /**
     * Obtiene los gastos del mes actual agrupados por categoría.
     */
    val categoryExpenses: StateFlow<List<CategoryExpenseData>> = userPreferences.userId
        .filterNotNull()
        .flatMapLatest { userId ->
            dailyExpenseDao.getExpensesByDateRange(userId, startOfMonth, endOfMonth)
                .map { expenses ->
                    expenses.groupBy { it.category }
                        .map { (category, expensesList) ->
                            CategoryExpenseData(
                                category = category,
                                total = expensesList.sumOf { it.amount },
                                count = expensesList.size
                            )
                        }
                        .sortedByDescending { it.total }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}