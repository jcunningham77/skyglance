package com.skyglance.location

actual fun createLocationProvider(platformContext: Any?): LocationProvider = UnsupportedLocationProvider()
