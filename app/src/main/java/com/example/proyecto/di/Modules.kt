package com.example.proyecto.di

import com.example.proyecto.presentation.detail.DetailViewModel
import com.example.proyecto.presentation.home.HomeViewModel
import com.example.proyecto.presentation.newprofile.NewProfileViewModel
import com.example.proyecto.presentation.flow.BudgetFlowViewModel
import com.example.proyecto.presentation.flow.BudgetFormViewModel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

private val vmModule = module {
    viewModel { HomeViewModel() }
    viewModel { NewProfileViewModel() }
    viewModel { DetailViewModel() }
    viewModel { BudgetFlowViewModel() }

    // Opción A (recomendada): evita inferencia
    viewModelOf(::BudgetFormViewModel)

    // Opción B (equivalente): con genérico explícito
    // viewModel<BudgetFormViewModel> { BudgetFormViewModel() }
}

val appModules = listOf(vmModule)
