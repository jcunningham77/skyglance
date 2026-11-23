package com.skyglance.data.mapper

import com.skyglance.domain.Condition
import kotlin.test.Test
import kotlin.test.assertEquals

class ConditionMapperTest {
    @Test
    fun mapsEveryWmoCodeRowFromThePlan() {
        val expected =
            mapOf(
                0 to Condition.Clear,
                1 to Condition.PartlyCloudy,
                2 to Condition.PartlyCloudy,
                3 to Condition.Overcast,
                45 to Condition.Fog,
                48 to Condition.Fog,
                51 to Condition.Drizzle,
                55 to Condition.Drizzle,
                57 to Condition.Drizzle,
                61 to Condition.Rain,
                65 to Condition.Rain,
                67 to Condition.Rain,
                71 to Condition.Snow,
                75 to Condition.Snow,
                77 to Condition.Snow,
                85 to Condition.Snow,
                86 to Condition.Snow,
                80 to Condition.Showers,
                81 to Condition.Showers,
                82 to Condition.Showers,
                95 to Condition.Thunderstorm,
                96 to Condition.Thunderstorm,
                99 to Condition.Thunderstorm,
            )

        expected.forEach { (code, condition) ->
            assertEquals(condition, code.toCondition(), "code $code")
        }
    }

    @Test
    fun unrecognizedCodeMapsToUnknown() {
        assertEquals(Condition.Unknown, 1000.toCondition())
        assertEquals(Condition.Unknown, (-1).toCondition())
        assertEquals(Condition.Unknown, 4.toCondition())
    }
}
