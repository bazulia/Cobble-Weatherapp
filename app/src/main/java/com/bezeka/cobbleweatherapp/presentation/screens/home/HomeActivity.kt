package com.bezeka.cobbleweatherapp.presentation.screens.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bezeka.cobbleweatherapp.R
import com.bezeka.cobbleweatherapp.presentation.util.LocationPermissionUtil
import com.bezeka.cobbleweatherapp.presentation.util.LocationPermissionUtil.checkLocationPermissions
import com.bezeka.cobbleweatherapp.presentation.util.getCityName
import com.bezeka.cobbleweatherapp.presentation.util.visibleOrGone
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val weatherItemsAdapter = WeatherItemsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupViews()

        viewModel.viewData().observe(this, { onViewData(it) })
        viewModel.event().observe(this, { onEvent(it) })
        viewModel.loading().observe(this, { progressBar.visibleOrGone(it) })

        tryRequestLocation()
    }

    private fun setupViews() {
        searchCityButton.setOnClickListener {
            Toast.makeText(this, "Not implemented yet :(", Toast.LENGTH_SHORT).show()
        }

        weatherDaysRecycler.layoutManager = LinearLayoutManager(this)
        weatherDaysRecycler.adapter = weatherItemsAdapter
    }

    private fun updateCurrentCity(location: Location) {
        currentCityTitle.text = getCityName(location)
    }

    private fun tryRequestLocation() {
        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            requestLastKnownLocation()
        } else {
            checkLocationPermissions(this, this::onLocationPermissionsGranted)
        }
    }

    private fun onViewData(viewData: ViewData) {
        weatherItemsAdapter.setItems(
            viewData.weatherDays.map { WeatherItem.from(resources, it) }
        )
    }

    private fun onEvent(event: Event) {
        when(event) {
            is Event.ErrorLoadingWeather -> {
                Toast.makeText(this, R.string.load_weather_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        LocationPermissionUtil.onRequestPermissionsResult(
            this,
            requestCode,
            this::onLocationPermissionsGranted
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestLastKnownLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    updateCurrentCity(location)
                    viewModel.requestWeatherForLocation(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, R.string.get_location_error, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun onLocationPermissionsGranted() {
        requestLastKnownLocation()
    }
}