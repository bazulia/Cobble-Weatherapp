package com.bezeka.cobbleweatherapp.domain

import com.bezeka.cobbleweatherapp.DI
import com.bezeka.cobbleweatherapp.domain.weather.WeatherInteractor
import com.bezeka.cobbleweatherapp.domain.weather.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun domainModule() = module {
    single<WeatherInteractor> {
        WeatherInteractor(
            get<WeatherRepository>(),
            get<CoroutineDispatcher>(qualifier = named(DI.DISPATCHER_IO))
        )
    }
}