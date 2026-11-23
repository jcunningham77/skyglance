package com.skyglance.ui.condition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.skyglance.domain.Condition

private data class GradientPair(
    val top: Color,
    val bottom: Color,
)

private val gradients: Map<Condition, GradientPair> =
    mapOf(
        Condition.Clear to GradientPair(Color(0xFF4A90D9), Color(0xFF8EC5F0)),
        Condition.PartlyCloudy to GradientPair(Color(0xFF6FA3D0), Color(0xFFB5C7D6)),
        Condition.Overcast to GradientPair(Color(0xFF7C8A97), Color(0xFFA9B3BC)),
        Condition.Fog to GradientPair(Color(0xFF9AA5AE), Color(0xFFC9CFD4)),
        Condition.Drizzle to GradientPair(Color(0xFF5E7384), Color(0xFF8798A6)),
        Condition.Rain to GradientPair(Color(0xFF2B4560), Color(0xFF121F2E)),
        Condition.Showers to GradientPair(Color(0xFF3B5570), Color(0xFF1A2B3D)),
        Condition.Snow to GradientPair(Color(0xFF8FA7BD), Color(0xFFDCE6EF)),
        Condition.Thunderstorm to GradientPair(Color(0xFF2A2540), Color(0xFF0B0A14)),
        Condition.Unknown to GradientPair(Color(0xFF5A6672), Color(0xFF3A434C)),
    )

private val scrim = Color.Black.copy(alpha = 0.45f)

/**
 * Condition-based gradient background with a theme-independent dark scrim, so detail text stays
 * legible in both light and dark mode. The final stretch phase swaps the gradient for a photo
 * behind the same scrim.
 */
@Composable
fun ConditionBackground(
    condition: Condition,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val pair = gradients.getValue(condition)
    Box(modifier = modifier.fillMaxSize().background(Brush.verticalGradient(listOf(pair.top, pair.bottom)))) {
        Box(modifier = Modifier.fillMaxSize().background(scrim))
        content()
    }
}
