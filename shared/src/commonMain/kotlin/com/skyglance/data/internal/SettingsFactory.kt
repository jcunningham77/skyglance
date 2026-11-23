package com.skyglance.data.internal

import com.russhwolf.settings.Settings

/** [platformContext] is the Android `Application` on Android (needed for `SharedPreferences`) and
 * unused on iOS (`NSUserDefaults` is a global singleton there). */
expect fun createSettings(platformContext: Any?): Settings
