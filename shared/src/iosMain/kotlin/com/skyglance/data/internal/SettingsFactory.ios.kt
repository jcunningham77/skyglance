package com.skyglance.data.internal

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

actual fun createSettings(platformContext: Any?): Settings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
