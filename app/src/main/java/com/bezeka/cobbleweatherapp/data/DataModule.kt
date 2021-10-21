package com.bezeka.cobbleweatherapp.data

import com.bezeka.cobbleweatherapp.BuildConfig
import com.bezeka.cobbleweatherapp.data.retrofit.RetrofitHolder
import com.bezeka.cobbleweatherapp.data.weather.WeatherApi
import com.bezeka.cobbleweatherapp.data.weather.WeatherRepositoryImpl
import com.bezeka.cobbleweatherapp.domain.weather.WeatherRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

const val BACKGROUND_EXECUTOR = "BACKGROUND_EXECUTOR"
const val BACKGROUND_DISPATCHER = "BACKGROUND_DISPATCHER"

const val GOOGLE_MAPS_API_KEY = "google_maps_api_key"
const val OPEN_WEATHER_MAP_API_KEY = "open_weather_map_api_key"

fun dataModule() = module {

    // threading
    single<Executor>(qualifier = named(BACKGROUND_EXECUTOR)) { Executors.newFixedThreadPool(5) }
    single<CoroutineDispatcher>(qualifier = named(BACKGROUND_DISPATCHER)) {
        get<Executor>(
            qualifier = named(
                BACKGROUND_EXECUTOR
            )
        ).asCoroutineDispatcher()
    }

    factory(qualifier = named(OPEN_WEATHER_MAP_API_KEY)) { BuildConfig.OWM_API_API_KEY }

    single<Moshi> {
        Moshi.Builder().build()
    }

    factory<OkHttpClient>() {
        createHttpClient()
    }

    single {
        RetrofitHolder(
            { get<OkHttpClient>() },
            get<Executor>(qualifier = named(BACKGROUND_EXECUTOR)),
            get<Moshi>()
        )
    }

    single<Retrofit> {
        Retrofit.Builder()
            .client(get<OkHttpClient>())
            .callbackExecutor(get<Executor>(qualifier = named(BACKGROUND_EXECUTOR)))
            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>()))
            .baseUrl(BuildConfig.OWM_API_BASE_URL)
            .build()
    }

    single<WeatherRepository> {
        WeatherRepositoryImpl(
            get<RetrofitHolder>(),
            get<String>(qualifier = named(OPEN_WEATHER_MAP_API_KEY))
        )
    }
}

private fun Scope.createHttpClient(): OkHttpClient {
    val httpClientBuilder = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))

    if (BuildConfig.DEBUG) {
        httpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }
    return httpClientBuilder.build()
}