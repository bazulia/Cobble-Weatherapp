package com.bezeka.cobbleweatherapp.presentation

import android.app.Application
import com.bezeka.cobbleweatherapp.BuildConfig
import com.bezeka.cobbleweatherapp.appModule
import com.bezeka.cobbleweatherapp.data.dataModule
import com.bezeka.cobbleweatherapp.domain.domainModule
import com.bezeka.cobbleweatherapp.presentation.screens.home.homeModule
import com.google.android.libraries.places.api.Places
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Places.initialize(this, BuildConfig.PLACES_API_KEY)

        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    appModule(),
                    domainModule(),
                    dataModule(),
                    homeModule()
                )
            )
        }
    }
}