package com.bezeka.cobbleweatherapp.presentation.screens.home

import android.annotation.SuppressLint
import android.content.res.Resources
import android.text.format.DateUtils
import androidx.annotation.DrawableRes
import com.bezeka.cobbleweatherapp.R
import com.bezeka.cobbleweatherapp.domain.weather.Weather
import com.bezeka.cobbleweatherapp.domain.weather.WeatherType
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class WeatherItem(
    val dayOfWeek: String,
    val dayAndMonth: String,
    val temperature: String,
    @DrawableRes val weatherStateIcon: Int,
    val sunriseTitle: String,
    val sunsetTitle: String,
    val descriptionTitle: String,
    val windSpeedTitle: String,
) {
    companion object {
        fun from(resources: Resources, weather: Weather) =
            with(weather) {
                WeatherItem(
                    dayOfWeek = dateTime.getFormattedDate(
                        resources,
                        DAY_OF_WEEK_PATTERN,
                        true
                    ),
                    dayAndMonth = dateTime.getFormattedDate(
                        resources,
                        MONTH_AND_DAY_PATTERN
                    ),
                    temperature = temperature.toInt().toString(),
                    weatherStateIcon = type.getWeatherIcon(),
                    sunriseTitle = sunrise.getFormattedDate(
                        resources,
                        TIME_PATTERN
                    ),
                    sunsetTitle = sunset.getFormattedDate(
                        resources,
                        TIME_PATTERN
                    ),
                    descriptionTitle = description.replaceFirstChar { it.uppercaseChar() },
                    windSpeedTitle = getFormattedWindSpeed()
                )
            }

        private const val DAY_OF_WEEK_PATTERN = "EEEE"
        private const val MONTH_AND_DAY_PATTERN = "MMMM d"
        private const val TIME_PATTERN = "h:mm a"

        @SuppressLint("SimpleDateFormat")
        private fun Long.getFormattedDate(
            resources: Resources,
            pattern: String,
            checkForToday: Boolean = false
        ): String {
            val sdf = SimpleDateFormat(pattern)
            val timeInMills = TimeUnit.SECONDS.toMillis(this)
            val netDate = Date(timeInMills)
            return if (checkForToday && DateUtils.isToday(timeInMills)) {
                resources.getString(R.string.today_title)
            } else {
                sdf.format(netDate)
            }
        }

        private fun WeatherType.getWeatherIcon() =
            when (this) {
                WeatherType.SUN -> R.drawable.ic_sun
                WeatherType.CLOUD -> R.drawable.ic_cloud
                WeatherType.THUNDERSTORM -> R.drawable.ic_cloud_flash
                WeatherType.RAIN -> R.drawable.ic_rain
                WeatherType.COULD_RAIN -> R.drawable.ic_cloud_rain
                WeatherType.CLOUD_SNOW -> R.drawable.ic_cloud_snow
                WeatherType.UNKNOWN -> -1
            }

        private fun Weather.getFormattedWindSpeed(): String {
            val directions = arrayOf("n", "ne", "e", "se", "s", "sw", "w", "nw")
            val formattedDirection = directions[(((windSpeed % 360).roundToInt() / 45) % 8)]
            return "$formattedDirection ${windSpeed.toInt()} mph"
        }
    }
}