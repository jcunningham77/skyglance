package com.skyglance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skyglance.domain.DayForecast
import com.skyglance.domain.TempUnit
import com.skyglance.ui.condition.ConditionBackground
import com.skyglance.ui.condition.toPresentation
import com.skyglance.ui.format.formatTemperature
import com.skyglance.ui.format.formatWindSpeed
import com.skyglance.ui.format.toFullDateLabel

/** One page of the day-selection pager: full date, condition label and the daily extras. A null
 * extra shows a dash rather than a blank or a broken layout. */
@Composable
fun DayDetailPane(
    day: DayForecast,
    unit: TempUnit,
    dayLabel: String,
    modifier: Modifier = Modifier,
) {
    val presentation = day.condition.toPresentation()
    ConditionBackground(condition = day.condition, modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(dayLabel, color = Color.White, style = MaterialTheme.typography.labelLarge)
            Text(day.date.toFullDateLabel(), color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Text(presentation.label, color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "${formatTemperature(day.highC, unit)} / ${formatTemperature(day.lowC, unit)}",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
            )
            DetailRow("Precipitation", day.precipProbabilityMax?.let { "$it%" })
            DetailRow("Wind", day.windMaxKmh?.let { formatWindSpeed(it, unit) })
            DetailRow("Sunrise", day.sunrise?.time?.toString())
            DetailRow("Sunset", day.sunset?.time?.toString())
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String?,
) {
    Text("$label: ${value ?: "—"}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
}
