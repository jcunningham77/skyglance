package com.skyglance.di

import org.koin.core.context.startKoin
import org.koin.dsl.module

/** DI bindings for composeApp/commonMain (ViewModels). Empty until Phase 1 defines them. */
val appModule = module {}

private var koinStarted = false

fun initKoin(platformContext: Any? = null) {
    if (!koinStarted) {
        koinStarted = true
        startKoin { modules(sharedModule(platformContext), appModule) }
    }
}
