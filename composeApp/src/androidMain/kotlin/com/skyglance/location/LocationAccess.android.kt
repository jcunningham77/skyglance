package com.skyglance.location

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

private const val PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION

@Composable
actual fun rememberLocationAccess(onResult: (granted: Boolean, permanentlyDenied: Boolean) -> Unit): LocationAccess {
    val context = LocalContext.current
    val activity = context as? Activity
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // Right after a live request's callback, "the system won't show a rationale" reliably
            // means "denied forever" (we just asked, so this isn't the "never asked" case that
            // looks identical to it before any request has ever been made).
            val permanentlyDenied =
                !granted && activity?.let { !ActivityCompat.shouldShowRequestPermissionRationale(it, PERMISSION) } == true
            onResult(granted, permanentlyDenied)
        }
    return remember(launcher, context) {
        object : LocationAccess {
            override fun request() {
                launcher.launch(PERMISSION)
            }

            override fun openSettings() {
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                context.startActivity(intent)
            }
        }
    }
}
