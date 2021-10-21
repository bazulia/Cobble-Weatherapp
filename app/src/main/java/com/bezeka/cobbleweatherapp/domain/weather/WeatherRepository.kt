package com.bezeka.cobbleweatherapp.domain.weather

import androidx.annotation.WorkerThread
import com.bezeka.cobbleweatherapp.domain.InteractorResult

interface WeatherRepository {
    @WorkerThread
    suspend fun loadWeather(lat: Double, lon: Double): InteractorResult<List<Weather>>
}