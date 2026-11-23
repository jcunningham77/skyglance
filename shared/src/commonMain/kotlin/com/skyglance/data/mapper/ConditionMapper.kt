package com.skyglance.data.mapper

import com.skyglance.domain.Condition

/** Maps an Open-Meteo WMO weather code (`daily.weather_code`) to [Condition]. Any code outside the
 * known ranges maps to [Condition.Unknown] rather than throwing — a day is never dropped just
 * because of an unrecognized code. */
fun Int.toCondition(): Condition =
    when (this) {
        0 -> Condition.Clear
        1, 2 -> Condition.PartlyCloudy
        3 -> Condition.Overcast
        45, 48 -> Condition.Fog
        in 51..57 -> Condition.Drizzle
        in 61..67 -> Condition.Rain
        in 71..77, 85, 86 -> Condition.Snow
        in 80..82 -> Condition.Showers
        in 95..99 -> Condition.Thunderstorm
        else -> Condition.Unknown
    }
