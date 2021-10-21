package com.bezeka.cobbleweatherapp.presentation.screens.home

import com.bezeka.cobbleweatherapp.domain.weather.WeatherInteractor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun homeModule() = module {
    viewModel {
        HomeViewModel(
            get<WeatherInteractor>()
        )
    }
}