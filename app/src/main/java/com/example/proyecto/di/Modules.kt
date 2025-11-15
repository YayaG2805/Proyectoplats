package com.example.proyecto.di

import androidx.room.Room
import com.example.proyecto.data.local.AppDatabase
import com.example.proyecto.data.repository.UserRepository
import com.example.proyecto.data.repository.UserRepositoryImpl
import com.example.proyecto.presentation.auth.AuthViewModel
import com.example.proyecto.presentation.detail.DetailViewModel
import com.example.proyecto.presentation.flow.BudgetFlowViewModel
import com.example.proyecto.presentation.flow.BudgetFormViewModel
import com.example.proyecto.presentation.history.HistoryViewModel
import com.example.proyecto.presentation.home.HomeViewModel
import com.example.proyecto.presentation.newprofile.NewProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

private val dataModule = module {
    // Base de datos Room
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "piggy-db"
        ).build()
    }

    single { get<AppDatabase>().userDao() }

    single<UserRepository> { UserRepositoryImpl(get()) }
}

private val vmModule = module {
    viewModel { HomeViewModel() }
    viewModel { NewProfileViewModel() }
    viewModel { DetailViewModel() }
    viewModel { BudgetFlowViewModel() }
    viewModelOf(::BudgetFormViewModel)

    viewModel { HistoryViewModel() }

    viewModel { AuthViewModel(get()) }
}

val appModules = listOf(
    dataModule,
    vmModule
)
