package com.skyglance.location

import androidx.compose.runtime.Composable

/**
 * Permission-request surface the common UI drives without ever mentioning Android. [request]
 * triggers the system permission dialog (a no-op on the iOS stub). [openSettings] deep-links to
 * the platform's app settings screen, for when the permission is denied forever.
 */
interface LocationAccess {
    fun request()

    fun openSettings()
}

/** [onResult] fires after a live request's callback: [granted] is the outcome, and
 * [permanentlyDenied] is only meaningful when [granted] is false — see the Android actual for how
 * it's derived. */
@Composable
expect fun rememberLocationAccess(onResult: (granted: Boolean, permanentlyDenied: Boolean) -> Unit): LocationAccess
