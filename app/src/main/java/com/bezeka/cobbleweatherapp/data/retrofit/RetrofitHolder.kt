package com.bezeka.cobbleweatherapp.data.retrofit

import com.bezeka.cobbleweatherapp.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.Executor

class RetrofitHolder(
    private val clientProvider: () -> OkHttpClient,
    private val executor: Executor,
    private val moshi: Moshi
) {

    private var httpClient: OkHttpClient? = null
    private var retrofit: Retrofit? = null
    private val services = hashMapOf<Class<*>, Any>()

    @Suppress("UNCHECKED_CAST")
    @Synchronized
    fun <T> getService(clazz: Class<T>): T {
        var result = services[clazz] as? T
        if (result != null) {
            return result
        }

        val retrofit = this.retrofit ?: Retrofit.Builder()
            .client(ensureHttpClient())
            .callbackExecutor(executor)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BuildConfig.OWM_API_BASE_URL)
            .build().also { this.retrofit = it }
        result = retrofit.create(clazz)
        services[clazz] = result as Any
        return result
    }

    private fun ensureHttpClient(): OkHttpClient {
        val client = httpClient ?: clientProvider.invoke()
        httpClient = client
        return client
    }
}