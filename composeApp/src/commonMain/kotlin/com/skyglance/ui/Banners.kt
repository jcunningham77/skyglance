package com.skyglance.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Shown on top of cached content when the last refresh failed — the app never goes blank when it
 * has something to show. */
@Composable
fun StaleBanner(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(color = MaterialTheme.colorScheme.errorContainer, modifier = modifier) {
        Row(modifier = Modifier.padding(12.dp)) {
            Text(message, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onErrorContainer)
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

/** Shown on top of cached content when permission is denied but a cache still exists. */
@Composable
fun PermissionBanner(
    permanently: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(color = MaterialTheme.colorScheme.secondaryContainer, modifier = modifier) {
        Row(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Location permission needed to refresh the forecast.",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            if (permanently) {
                Button(onClick = onOpenSettings) { Text("Open settings") }
            } else {
                Button(onClick = onRequestPermission) { Text("Allow location") }
            }
        }
    }
}
