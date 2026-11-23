package com.skyglance.di

import com.skyglance.presentation.ForecastViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** DI bindings for composeApp/commonMain (ViewModels). */
val appModule =
    module {
        viewModel { ForecastViewModel(get(), get(), get()) }
    }

private var koinStarted = false

fun initKoin(platformContext: Any? = null) {
    if (!koinStarted) {
        koinStarted = true
        startKoin { modules(sharedModule(platformContext), appModule) }
    }
}
