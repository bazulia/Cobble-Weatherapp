package com.bezeka.cobbleweatherapp.presentation.screens.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bezeka.cobbleweatherapp.R
import com.bezeka.cobbleweatherapp.presentation.util.DiffCallBack
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_weather_day.view.*

class WeatherItemsAdapter : RecyclerView.Adapter<WeatherItemsViewHolder>() {

    private var weatherItems = listOf<WeatherItem>()

    fun setItems(newList: List<WeatherItem>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallBack(newList, weatherItems))
        weatherItems = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherItemsViewHolder {
        return WeatherItemsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_weather_day, parent, false),
        )
    }

    override fun onBindViewHolder(holder: WeatherItemsViewHolder, position: Int) {
        holder.bind(weatherItems[position])
    }

    override fun getItemCount(): Int = weatherItems.size
}

class WeatherItemsViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

    override val containerView: View
        get() = itemView

    private val dayOfWeekTitle = view.dayOfWeekTitle
    private val dayAndMonthTitle = view.dayAndMonthTitle
    private val temperatureTitle = view.temperatureTitle
    private val weatherStateIcon = view.weatherStateIcon
    private val sunriseTitle = view.sunriseTitle
    private val sunsetTitle = view.sunsetTitle
    private val descriptionTitle = view.descriptionTitle
    private val windSpeedTitle = view.windSpeedTitle

    @SuppressLint("SetTextI18n")
    fun bind(weatherItem: WeatherItem) {
        dayOfWeekTitle.text = weatherItem.dayOfWeek
        dayAndMonthTitle.text = weatherItem.dayAndMonth
        temperatureTitle.text = weatherItem.temperature
        weatherStateIcon.setImageResource(weatherItem.weatherStateIcon)
        sunriseTitle.text = weatherItem.sunriseTitle
        sunsetTitle.text = weatherItem.sunsetTitle
        descriptionTitle.text = weatherItem.descriptionTitle
        windSpeedTitle.text = weatherItem.windSpeedTitle
    }
}