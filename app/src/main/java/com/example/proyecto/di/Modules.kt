package com.example.proyecto.di

import androidx.room.Room
import com.example.proyecto.data.local.AppDatabase
import com.example.proyecto.data.local.UserPreferences
import com.example.proyecto.data.repository.UserRepository
import com.example.proyecto.data.repository.UserRepositoryImpl
import com.example.proyecto.presentation.auth.AuthViewModel
import com.example.proyecto.presentation.budgetstatus.BudgetStatusViewModel
import com.example.proyecto.presentation.dailyexpense.DailyExpenseViewModel
import com.example.proyecto.presentation.detail.DetailViewModel
import com.example.proyecto.presentation.flow.BudgetFlowViewModel
import com.example.proyecto.presentation.flow.TipsBottomNavViewModel
import com.example.proyecto.presentation.flow.TipsViewModel
import com.example.proyecto.presentation.history.HistoryViewModel
import com.example.proyecto.presentation.home.HomeViewModel
import com.example.proyecto.presentation.newprofile.NewProfileViewModel
import com.example.proyecto.presentation.profile.ProfileViewModel
import com.example.proyecto.presentation.savingsindex.SavingsIndexViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val dataModule = module {
    // Base de datos Room
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "piggy-db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAOs
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().dailyExpenseDao() }
    single { get<AppDatabase>().monthlyBudgetDao() }

    // DataStore
    single { UserPreferences(androidContext()) }

    // Repositories
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}

private val vmModule = module {
    // ViewModels básicos
    viewModel { HomeViewModel() }
    viewModel { DetailViewModel() }
    viewModel { BudgetFlowViewModel() }

    // HistoryViewModel - SINGLETON para compartir datos
    single { HistoryViewModel(get(), get()) }

    // AuthViewModel
    viewModel { AuthViewModel(get(), get()) }

    // DailyExpenseViewModel - Con MonthlyBudgetDao
    viewModel { DailyExpenseViewModel(get(), get(), get()) }

    // NewProfileViewModel - CORREGIDO: Ahora usa MonthlyBudgetDao y UserPreferences
    viewModel { NewProfileViewModel(get(), get()) }

    // ProfileViewModel - Con todos los DAOs necesarios
    viewModel { ProfileViewModel(get(), get(), get(), get(), get()) }

    // TipsBottomNavViewModel
    viewModel { TipsBottomNavViewModel(get()) }

    // TipsViewModel - Para obtener gastos por categoría
    viewModel { TipsViewModel(get(), get()) }

    // BudgetStatusViewModel - Sincroniza gastos diarios con presupuesto mensual
    viewModel { BudgetStatusViewModel(get(), get(), get()) }

    // SavingsIndexViewModel - Para mostrar índice de ahorro con gráficas
    viewModel { SavingsIndexViewModel(get(), get()) }
}

val appModules = listOf(
    dataModule,
    vmModule
)