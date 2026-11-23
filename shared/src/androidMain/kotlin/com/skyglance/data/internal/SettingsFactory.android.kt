package com.skyglance.data.internal

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

actual fun createSettings(platformContext: Any?): Settings {
    val context = platformContext as Context
    val prefs = context.getSharedPreferences("skyglance_settings", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(prefs)
}
