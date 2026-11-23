package com.skyglance.location

/** [platformContext] is the Android `Application` on Android (needed for `FusedLocationProviderClient`)
 * and unused on iOS (the stub needs no context). */
expect fun createLocationProvider(platformContext: Any?): LocationProvider
