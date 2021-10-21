package com.bezeka.cobbleweatherapp.data.weather

import com.bezeka.cobbleweatherapp.domain.weather.Weather
import com.bezeka.cobbleweatherapp.domain.weather.WeatherType
import com.squareup.moshi.Json
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val ARG_LAT = "lat"
private const val ARG_LON = "lon"
private const val ARG_API_KEY = "appid"
private const val ARG_UNITS_KEY = "units"
private const val ARG_UNITS_IMPERIAL = "imperial"
private const val ARG_EXCLUDE_KEY = "exclude"
private const val ARG_EXCLUDE_ALERTS = "alerts"
private const val ARG_EXCLUDE_CURRENT = "current"
private const val ARG_EXCLUDE_MINUTELY = "minutely"
private const val ARG_EXCLUDE_HOURLY = "hourly"
private const val ARG_EXCLUDE_DAILY = "daily"
private const val PATH_GET_WEATHER = "onecall?"
private val DEFAULT_EXCLUDE_ARRAY = arrayOf(
    ARG_EXCLUDE_CURRENT,
    ARG_EXCLUDE_MINUTELY,
    ARG_EXCLUDE_HOURLY,
    ARG_EXCLUDE_ALERTS
)
interface WeatherApi {

    @GET(PATH_GET_WEATHER)
    fun getWeather(
        @Query(ARG_LAT) lat: Double,
        @Query(ARG_LON) lon: Double,
        @Query(ARG_API_KEY) apiKey: String,
        @Query(ARG_EXCLUDE_KEY) exclude: Array<String> = DEFAULT_EXCLUDE_ARRAY,
        @Query(ARG_UNITS_KEY) units: String = ARG_UNITS_IMPERIAL
    ): Call<WeatherResponse>

}

data class WeatherResponse(
    @field:Json(name="lat")
    val lat: Double,
    @field:Json(name="lon")
    val lon: Double,
    @field:Json(name="daily")
    val daily: Array<DailyWeatherResponse>
) {
    fun toDomain(): List<Weather> = daily.map {
        Weather(
            it.sunrise,
            it.sunset,
            it.dateTime,
            it.temp.day,
            it.windSpeed,
            WeatherType.from(it.weather.firstOrNull()?.id ?: 0),
            it.weather.firstOrNull()?.description.orEmpty()
        )
    }
}

data class DailyWeatherResponse(
    @field:Json(name="sunrise")
    val sunrise: Long,
    @field:Json(name="sunset")
    val sunset: Long,
    @field:Json(name="dt")
    val dateTime: Long,
    @field:Json(name="temp")
    val temp: TempResponse,
    @field:Json(name="wind_speed")
    val windSpeed: Double,
    @field:Json(name="weather")
    val weather: Array<DescriptionResponse>
)

data class TempResponse(
    @field:Json(name="day")
    val day: Double
)

data class DescriptionResponse(
    @field:Json(name="id")
    val id: Int,
    @field:Json(name="description")
    val description: String,
)