package com.skyglance.ui.condition

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.skyglance.domain.Condition
import com.skyglance.generated.resources.Res
import com.skyglance.generated.resources.bg_clear
import com.skyglance.generated.resources.bg_fog
import com.skyglance.generated.resources.bg_overcast
import com.skyglance.generated.resources.bg_partly_cloudy
import com.skyglance.generated.resources.bg_rain
import com.skyglance.generated.resources.bg_snow
import com.skyglance.generated.resources.bg_thunderstorm
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Seven bundled photos cover every condition but [Condition.Unknown], which falls back to a
 * neutral gradient — simpler than sourcing an eighth "nothing in particular" photo, and "unknown"
 * should read as unknown rather than confidently wrong.
 */
private val photos: Map<Condition, DrawableResource> =
    mapOf(
        Condition.Clear to Res.drawable.bg_clear,
        Condition.PartlyCloudy to Res.drawable.bg_partly_cloudy,
        Condition.Overcast to Res.drawable.bg_overcast,
        Condition.Fog to Res.drawable.bg_fog,
        Condition.Drizzle to Res.drawable.bg_rain,
        Condition.Rain to Res.drawable.bg_rain,
        Condition.Showers to Res.drawable.bg_rain,
        Condition.Snow to Res.drawable.bg_snow,
        Condition.Thunderstorm to Res.drawable.bg_thunderstorm,
    )

private val unknownGradient = Brush.verticalGradient(listOf(Color(0xFF5A6672), Color(0xFF3A434C)))

private val scrim = Color.Black.copy(alpha = 0.45f)

/**
 * Condition-based photo background with a theme-independent dark scrim, so detail text stays
 * legible in both light and dark mode.
 */
@Composable
fun ConditionBackground(
    condition: Condition,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        val photo = photos[condition]
        if (photo != null) {
            Image(
                painter = painterResource(photo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(unknownGradient))
        }
        Box(modifier = Modifier.fillMaxSize().background(scrim))
        content()
    }
}
