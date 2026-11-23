package com.skyglance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skyglance.domain.DayForecast
import com.skyglance.domain.TempUnit
import com.skyglance.ui.condition.toPresentation
import com.skyglance.ui.format.formatTemperature
import com.skyglance.ui.format.toWeekdayLabel

/** Equal-width row of day boxes: weekday, condition glyph and two temperatures only — the
 * condition label lives in the detail pane so this stays readable at 320 dp wide. */
@Composable
fun DaySummaryRow(
    days: List<DayForecast>,
    selectedIndex: Int,
    unit: TempUnit,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        days.forEachIndexed { index, day ->
            DaySummaryBox(
                day = day,
                unit = unit,
                isSelected = index == selectedIndex,
                onClick = { onDaySelected(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun DaySummaryBox(
    day: DayForecast,
    unit: TempUnit,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val presentation = day.condition.toPresentation()
    val background = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    Column(
        modifier =
            modifier
                .selectable(selected = isSelected, onClick = onClick)
                .background(background, RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(day.date.toWeekdayLabel(), style = MaterialTheme.typography.labelMedium)
        Text(
            text = presentation.glyph,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.clearAndSetSemantics { contentDescription = presentation.contentDescription },
        )
        Text(formatTemperature(day.highC, unit), fontWeight = FontWeight.SemiBold)
        Text(formatTemperature(day.lowC, unit), style = MaterialTheme.typography.bodySmall)
    }
}
