package com.bezeka.cobbleweatherapp.presentation.screens.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import com.bezeka.cobbleweatherapp.presentation.util.getCity
import com.bezeka.cobbleweatherapp.presentation.util.visibleOrGone
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val AUTOCOMPLETE_REQUEST_CODE = 1

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
            openCitySearch()
        }

        weatherDaysRecycler.layoutManager = LinearLayoutManager(this)
        weatherDaysRecycler.adapter = weatherItemsAdapter
    }

    private fun openCitySearch() {
        // also can be replaced with using Places Api directly
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY,
            listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        ).build(this)
        // todo should be replaces with registerActivityLifecycleCallbacks(callback)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun updateCurrentCity(latitude: Double, longitude: Double) {
        currentCityTitle.text = getCity(latitude, longitude)
    }

    private fun updateCurrentCity(city: String) {
        currentCityTitle.text = city
    }

    private fun tryRequestLocation() {
        if (ActivityCompat.checkSelfPermission(
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
        when (event) {
            is Event.ErrorLoadingWeather -> {
                Toast.makeText(this, R.string.load_weather_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        place.name?.let { updateCurrentCity(it) }
                        place.latLng?.let {
                            viewModel.requestWeatherForLocation(it.latitude, it.longitude)
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Toast.makeText(
                            this,
                            getString(R.string.load_places_error, status.statusMessage),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
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
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    updateCurrentCity(location.latitude, location.longitude)
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