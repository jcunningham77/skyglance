package com.skyglance.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// Stub: location is unsupported on iOS for now (see UnsupportedLocationProvider), so
// ForecastUiState.PermissionNeeded is never actually reachable here — request()/openSettings()
// are unreachable in practice but still need to exist to satisfy the expect/actual contract.
@Composable
actual fun rememberLocationAccess(onResult: (granted: Boolean, permanentlyDenied: Boolean) -> Unit): LocationAccess =
    remember {
        object : LocationAccess {
            override fun request() {}

            override fun openSettings() {}
        }
    }
