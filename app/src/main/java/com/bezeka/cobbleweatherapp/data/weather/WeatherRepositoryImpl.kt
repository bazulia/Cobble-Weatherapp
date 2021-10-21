package com.bezeka.cobbleweatherapp.data.weather

import androidx.annotation.WorkerThread
import com.bezeka.cobbleweatherapp.domain.InteractorResult
import com.bezeka.cobbleweatherapp.data.retrofit.RetrofitHolder
import com.bezeka.cobbleweatherapp.domain.weather.Weather
import com.bezeka.cobbleweatherapp.domain.weather.WeatherRepository
import retrofit2.awaitResponse

class WeatherRepositoryImpl(
    private val retrofitHolder: RetrofitHolder,
    private val apiKey: String,
) : WeatherRepository {

    private val weatherApi: WeatherApi
        get() = retrofitHolder.getService(WeatherApi::class.java)

    @WorkerThread
    override suspend fun loadWeather(
        lat: Double,
        lon: Double
    ): InteractorResult<List<Weather>> {
        return try {
            val data = weatherApi.getWeather(lat, lon, apiKey).execute().body()
            if (data != null) {
                InteractorResult.Success(data.toDomain())
            } else {
                InteractorResult.Error(null, Exception("Something went wrong"))
            }
        } catch (e: Exception) {
            InteractorResult.Error(null, e)
        }
    }
}