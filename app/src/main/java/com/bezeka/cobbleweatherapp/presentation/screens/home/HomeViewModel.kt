package com.bezeka.cobbleweatherapp.presentation.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bezeka.cobbleweatherapp.domain.InteractorResult
import com.bezeka.cobbleweatherapp.domain.weather.Weather
import com.bezeka.cobbleweatherapp.domain.weather.WeatherInteractor
import com.bezeka.cobbleweatherapp.presentation.util.LiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherInteractor: WeatherInteractor
) : ViewModel(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main.immediate + Job()

    private val event = LiveEvent<Event>()
    fun event(): LiveEvent<Event> = event

    private val viewData = MutableLiveData<ViewData>()
    fun viewData(): LiveData<ViewData> = viewData

    private val loading = MutableLiveData<Boolean>()
    fun loading(): LiveData<Boolean> = loading

    init {
        loading.value = true
    }

    fun requestWeatherForLocation(lat: Double, lon: Double) {
        launch {
            loading.value = true
            val result = weatherInteractor.getWeather(lat, lon)
            loading.value = false
            when(result) {
                is InteractorResult.Success -> {
                    viewData.value = ViewData(result.data)
                }
                is InteractorResult.Error -> {
                    event.value = Event.ErrorLoadingWeather
                }
            }
        }
    }

}

sealed class Event {
    object ErrorLoadingWeather: Event()
}

data class ViewData(
    val weatherDays: List<Weather>
)