package com.skyglance.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skyglance.domain.TempUnit

/** Switching units never triggers a refetch — it's a pure display function over the °C cache. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitToggle(
    unit: TempUnit,
    onUnitSelected: (TempUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(TempUnit.Fahrenheit, TempUnit.Celsius)
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = unit == option,
                onClick = { onUnitSelected(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
            ) {
                Text(if (option == TempUnit.Fahrenheit) "°F" else "°C")
            }
        }
    }
}
