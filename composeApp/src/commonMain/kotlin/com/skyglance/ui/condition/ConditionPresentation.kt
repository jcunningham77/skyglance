package com.skyglance.ui.condition

import com.skyglance.domain.Condition

/**
 * UI-only presentation for [Condition]: label, a glyph (Phase 1 placeholder for a real icon set —
 * see the plan's final stretch phase for bundled condition imagery) and an accessibility
 * description. Keeps formatted strings out of the domain model.
 */
data class ConditionPresentation(
    val label: String,
    val glyph: String,
    val contentDescription: String,
)

fun Condition.toPresentation(): ConditionPresentation =
    when (this) {
        Condition.Clear -> ConditionPresentation("Clear", "☀️", "Clear sky")
        Condition.PartlyCloudy -> ConditionPresentation("Partly cloudy", "⛅", "Partly cloudy")
        Condition.Overcast -> ConditionPresentation("Overcast", "☁️", "Overcast")
        Condition.Fog -> ConditionPresentation("Fog", "🌫️", "Fog")
        Condition.Drizzle -> ConditionPresentation("Drizzle", "🌦️", "Drizzle")
        Condition.Rain -> ConditionPresentation("Rain", "🌧️", "Rain")
        Condition.Snow -> ConditionPresentation("Snow", "❄️", "Snow")
        Condition.Showers -> ConditionPresentation("Showers", "🌧️", "Showers")
        Condition.Thunderstorm -> ConditionPresentation("Thunderstorm", "⛈️", "Thunderstorm")
        Condition.Unknown -> ConditionPresentation("Unknown", "❓", "Unknown condition")
    }
