package com.skyglance.data.dto

import kotlinx.serialization.Serializable

/** BigDataCloud's reverse-geocode response. Only the fields used to build a place label are
 * modeled; everything else is ignored via `ignoreUnknownKeys`. */
@Serializable
data class GeocodeResponseDto(
    val city: String? = null,
    val locality: String? = null,
    val principalSubdivision: String? = null,
    val countryName: String? = null,
)

/** "City, Region" when both are present, falling back to whichever single part is available, or
 * null if the response had nothing usable — the UI then falls back to coordinates. */
fun GeocodeResponseDto.toPlaceName(): String? {
    val locality = city?.takeIf { it.isNotBlank() } ?: locality?.takeIf { it.isNotBlank() }
    val region = principalSubdivision?.takeIf { it.isNotBlank() } ?: countryName?.takeIf { it.isNotBlank() }
    return when {
        locality != null && region != null -> "$locality, $region"
        locality != null -> locality
        region != null -> region
        else -> null
    }
}
