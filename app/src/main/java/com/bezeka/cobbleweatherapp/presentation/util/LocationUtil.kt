package com.bezeka.cobbleweatherapp.presentation.util

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.location.Location
import java.util.*

fun Activity.getCityName(location: Location): String {
    val geocoder = Geocoder(this, Locale.getDefault())
    val addresses: List<Address> = geocoder.getFromLocation(
        location.latitude,
        location.longitude,
        1
    )
    return addresses.first().locality
}