package com.skyglance.location

import android.content.Context

actual fun createLocationProvider(platformContext: Any?): LocationProvider = FusedLocationProvider(platformContext as Context)
