package com.bezeka.cobbleweatherapp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

object DI {
    const val DISPATCHER_UI = "DISPATCHER_UI"
    const val DISPATCHER_IO = "DISPATCHER_IO"
}

fun appModule() = module {

    single<CoroutineDispatcher>(qualifier = named(DI.DISPATCHER_UI)) { Dispatchers.Main }
    single<CoroutineDispatcher>(qualifier = named(DI.DISPATCHER_IO)) { Dispatchers.IO }


}