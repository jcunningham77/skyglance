package com.skyglance.ui.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyglance.domain.Condition
import com.skyglance.domain.TempUnit
import com.skyglance.ui.DayDetailPane
import com.skyglance.ui.DaySummaryBox
import com.skyglance.ui.DaySummaryRow
import com.skyglance.ui.LocationHeader
import com.skyglance.ui.PermissionBanner
import com.skyglance.ui.StaleBanner
import com.skyglance.ui.UnitToggle
import com.skyglance.ui.condition.ConditionBackground

// region Day summary box

@Preview
@Composable
internal fun DaySummaryBoxPreview_Normal() =
    ThemedPreview { DaySummaryBox(PreviewData.sevenDays[1], TempUnit.Fahrenheit, isSelected = false, onClick = {}) }

@Preview
@Composable
internal fun DaySummaryBoxPreview_Selected() =
    ThemedPreview { DaySummaryBox(PreviewData.sevenDays[0], TempUnit.Fahrenheit, isSelected = true, onClick = {}) }

@Preview
@Composable
internal fun DaySummaryBoxPreview_Negative() =
    ThemedPreview { DaySummaryBox(PreviewData.negativeDay, TempUnit.Fahrenheit, isSelected = false, onClick = {}) }

@Preview
@Composable
internal fun DaySummaryBoxPreview_ThreeDigit() =
    ThemedPreview { DaySummaryBox(PreviewData.threeDigitDay, TempUnit.Fahrenheit, isSelected = false, onClick = {}) }

@Preview
@Composable
internal fun DaySummaryBoxPreview_MissingDataUnknownCondition() =
    ThemedPreview { DaySummaryBox(PreviewData.sevenDaysWithMissingData.last(), TempUnit.Fahrenheit, isSelected = false, onClick = {}) }

// endregion

// region Day summary row

@Preview
@Composable
internal fun DaySummaryRowPreview_TodaySelected() =
    ThemedPreview { DaySummaryRow(PreviewData.sevenDays, selectedIndex = 0, unit = TempUnit.Fahrenheit, onDaySelected = {}) }

@Preview
@Composable
internal fun DaySummaryRowPreview_MiddleDaySelected() =
    ThemedPreview { DaySummaryRow(PreviewData.sevenDays, selectedIndex = 3, unit = TempUnit.Fahrenheit, onDaySelected = {}) }

@Preview
@Composable
internal fun DaySummaryRowPreview_SixDays() =
    ThemedPreview { DaySummaryRow(PreviewData.sixDays, selectedIndex = 0, unit = TempUnit.Fahrenheit, onDaySelected = {}) }

@Preview(widthDp = 320)
@Composable
internal fun DaySummaryRowPreview_NarrowPhone() =
    ThemedPreview { DaySummaryRow(PreviewData.sevenDays, selectedIndex = 0, unit = TempUnit.Fahrenheit, onDaySelected = {}) }

@Preview(fontScale = 1.8f)
@Composable
internal fun DaySummaryRowPreview_LargeFontScale() =
    ThemedPreview { DaySummaryRow(PreviewData.sevenDays, selectedIndex = 0, unit = TempUnit.Fahrenheit, onDaySelected = {}) }

// endregion

// region Day detail pane

@Preview
@Composable
internal fun DayDetailPanePreview_FullData() =
    ThemedPreview {
        Box(Modifier.fillMaxWidth().height(400.dp)) {
            DayDetailPane(PreviewData.sevenDays[0], TempUnit.Fahrenheit, dayLabel = "Today")
        }
    }

@Preview
@Composable
internal fun DayDetailPanePreview_NullExtras() =
    ThemedPreview {
        Box(Modifier.fillMaxWidth().height(400.dp)) {
            DayDetailPane(PreviewData.nullExtrasDay, TempUnit.Fahrenheit, dayLabel = "Mon")
        }
    }

@Preview
@Composable
internal fun DayDetailPanePreview_LongConditionLabel() =
    ThemedPreview {
        Box(Modifier.fillMaxWidth().height(400.dp)) {
            DayDetailPane(PreviewData.sevenDays[3], TempUnit.Celsius, dayLabel = "Fri")
        }
    }

// endregion

// region Location header

@Preview
@Composable
internal fun LocationHeaderPreview_PlaceName() = ThemedPreview { LocationHeader(placeName = "Boulder, Colorado", lat = 40.0, lon = -105.0) }

@Preview
@Composable
internal fun LocationHeaderPreview_CoordinatesFallback() =
    ThemedPreview { LocationHeader(placeName = null, lat = 40.0150, lon = -105.2705) }

@Preview
@Composable
internal fun LocationHeaderPreview_LongName() =
    ThemedPreview {
        LocationHeader(
            placeName = PreviewData.forecastLongPlaceName.placeName,
            lat = 40.0,
            lon = -105.0,
            modifier = Modifier.fillMaxWidth(),
        )
    }

// endregion

// region Unit toggle

@Preview
@Composable
internal fun UnitTogglePreview_Fahrenheit() = ThemedPreview { UnitToggle(unit = TempUnit.Fahrenheit, onUnitSelected = {}) }

@Preview
@Composable
internal fun UnitTogglePreview_Celsius() = ThemedPreview { UnitToggle(unit = TempUnit.Celsius, onUnitSelected = {}) }

// endregion

// region Banners

@Preview
@Composable
internal fun StaleBannerPreview() = ThemedPreview { StaleBanner("Last updated 3 h ago, couldn't refresh", onRetry = {}) }

@Preview
@Composable
internal fun PermissionBannerPreview_CanAskAgain() =
    ThemedPreview { PermissionBanner(permanently = false, onRequestPermission = {}, onOpenSettings = {}) }

@Preview
@Composable
internal fun PermissionBannerPreview_PermanentlyDenied() =
    ThemedPreview { PermissionBanner(permanently = true, onRequestPermission = {}, onOpenSettings = {}) }

// endregion

// region Condition background with scrim — theme-independent, so one preview per condition.

@Preview
@Composable
internal fun ConditionBackgroundPreview_Clear() = ConditionGradientPreview(Condition.Clear)

@Preview
@Composable
internal fun ConditionBackgroundPreview_PartlyCloudy() = ConditionGradientPreview(Condition.PartlyCloudy)

@Preview
@Composable
internal fun ConditionBackgroundPreview_Overcast() = ConditionGradientPreview(Condition.Overcast)

@Preview
@Composable
internal fun ConditionBackgroundPreview_Fog() = ConditionGradientPreview(Condition.Fog)

@Preview
@Composable
internal fun ConditionBackgroundPreview_Drizzle() = ConditionGradientPreview(Condition.Drizzle)

@Preview
@Composable
internal fun ConditionBackgroundPreview_Rain() = ConditionGradientPreview(Condition.Rain)

@Preview
@Composable
internal fun ConditionBackgroundPreview_Snow() = ConditionGradientPreview(Condition.Snow)

@Preview
@Composable
internal fun ConditionBackgroundPreview_Showers() = ConditionGradientPreview(Condition.Showers)

@Preview
@Composable
internal fun ConditionBackgroundPreview_Thunderstorm() = ConditionGradientPreview(Condition.Thunderstorm)

@Preview
@Composable
internal fun ConditionBackgroundPreview_Unknown() = ConditionGradientPreview(Condition.Unknown)

@Composable
private fun ConditionGradientPreview(condition: Condition) {
    Box(Modifier.fillMaxWidth().height(160.dp)) {
        ConditionBackground(condition = condition) {
            Text(condition.name, color = Color.White, modifier = Modifier.padding(16.dp))
        }
    }
}

// endregion

// region Day selection — interactive demo host (tap a box; the pager-driven swipe lives in
// ScreenStatePreviews, which exercises the full ForecastContent)

@Preview
@Composable
internal fun DaySelectionDemoPreview_TapToSelect() {
    var selectedIndex by remember { mutableStateOf(0) }
    ThemedPreview {
        DaySummaryRow(
            days = PreviewData.sevenDays,
            selectedIndex = selectedIndex,
            unit = TempUnit.Fahrenheit,
            onDaySelected = { selectedIndex = it },
        )
    }
}

@Preview
@Composable
internal fun DaySelectionDemoPreview_LastDaySelected() =
    ThemedPreview {
        DaySummaryRow(
            days = PreviewData.sevenDays,
            selectedIndex = PreviewData.sevenDays.lastIndex,
            unit = TempUnit.Fahrenheit,
            onDaySelected = {},
        )
    }

// endregion
