package com.bezeka.cobbleweatherapp.domain.weather

import com.bezeka.cobbleweatherapp.domain.InteractorResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class WeatherInteractor(
    private val weatherRepository: WeatherRepository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getWeather(lat: Double, lon: Double): InteractorResult<List<Weather>> {
        return withContext(ioDispatcher) {
            weatherRepository.loadWeather(lat, lon)
        }
    }

}