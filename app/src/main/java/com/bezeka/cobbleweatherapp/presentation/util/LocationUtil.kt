package com.bezeka.cobbleweatherapp.presentation.util

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import java.util.*

fun Activity.getCity(latitude: Double, longitude: Double): String {
    val geocoder = Geocoder(this, Locale.getDefault())
    val addresses: List<Address> = geocoder.getFromLocation(
        latitude,
        longitude,
        1
    )
    return addresses.first().locality
}