package com.skyglance.di

import org.koin.dsl.module

/**
 * DI bindings for shared/commonMain (repositories, API clients, cache). Empty until the
 * data layer is built; [com.skyglance.di.initKoin] wires it alongside the UI module.
 */
val sharedModule = module {}
