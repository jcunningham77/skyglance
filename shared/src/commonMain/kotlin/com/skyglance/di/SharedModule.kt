package com.skyglance.di

import com.russhwolf.settings.Settings
import com.skyglance.data.ForecastCache
import com.skyglance.data.ForecastRepository
import com.skyglance.data.ForecastRepositoryImpl
import com.skyglance.data.GeocodeApi
import com.skyglance.data.PreferencesRepository
import com.skyglance.data.PreferencesRepositoryImpl
import com.skyglance.data.WeatherApi
import com.skyglance.data.internal.createHttpClient
import com.skyglance.data.internal.createSettings
import com.skyglance.location.LocationProvider
import com.skyglance.location.UnimplementedLocationProvider
import io.ktor.client.HttpClient
import org.koin.dsl.module
import kotlin.time.Clock

/**
 * DI bindings for shared/commonMain (repositories, API clients, cache). [platformContext] is the
 * Android `Application` on Android, unused on iOS — see [createSettings].
 *
 * [LocationProvider] is [UnimplementedLocationProvider] for now; the Android location phase
 * replaces this with a real `FusedLocationProviderClient`-backed implementation and an iOS stub.
 */
fun sharedModule(platformContext: Any?) =
    module {
        single<Settings> { createSettings(platformContext) }
        single<HttpClient> { createHttpClient() }
        single<Clock> { Clock.System }

        single { WeatherApi(get()) }
        single { GeocodeApi(get()) }
        single { ForecastCache(get()) }

        single<LocationProvider> { UnimplementedLocationProvider() }
        single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
        single<ForecastRepository> { ForecastRepositoryImpl(get(), get(), get(), get(), get()) }
    }
