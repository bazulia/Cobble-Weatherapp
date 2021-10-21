package com.bezeka.cobbleweatherapp.domain.weather

import com.squareup.moshi.Json

data class Weather(
    val sunrise: Long,
    val sunset: Long,
    val dateTime: Long,
    val temperature: Double,
    val windSpeed: Double,
    val type: WeatherType,
    val description: String
)

enum class WeatherType {
    SUN, CLOUD, THUNDERSTORM, RAIN, COULD_RAIN, CLOUD_SNOW, UNKNOWN;

    companion object {
        fun from(code: Int) =
            when(code) {
                in 200..299 -> THUNDERSTORM
                in 300..399 -> COULD_RAIN
                in 400..499 -> COULD_RAIN
                in 500..599 -> RAIN
                in 600..699 -> CLOUD_SNOW
                in 700..799 -> CLOUD
                in 800..899 -> SUN
                in 900..999 -> CLOUD
                else -> UNKNOWN
            }
    }
}