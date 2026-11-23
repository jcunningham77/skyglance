package com.skyglance.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlin.math.round

/** Place name from reverse geocoding, falling back to rounded coordinates if that failed. */
@Composable
fun LocationHeader(
    placeName: String?,
    lat: Double,
    lon: Double,
    modifier: Modifier = Modifier,
) {
    val label = placeName ?: "${roundCoordinate(lat)}, ${roundCoordinate(lon)}"
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

private fun roundCoordinate(value: Double): String = (round(value * 100) / 100).toString()
