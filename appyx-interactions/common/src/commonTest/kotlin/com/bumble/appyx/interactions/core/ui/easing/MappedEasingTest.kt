package com.bumble.appyx.interactions.core.ui.easing

import androidx.compose.animation.core.LinearEasing
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MappedEasingTest {

    companion object {
        private const val lt_min = 0.2f
        private const val min = 0.6f
        private const val halfway_min_max = 0.7f
        private const val max = 0.8f
        private const val gt_max = 0.9f
    }

    private val mappedEasing = MappedEasing(
        min = min,
        max = max,
        easing = LinearEasing
    )

    @Test
    fun WHEN_fraction_negative_THEN_easing_invoked_with_0() {
        val actual = mappedEasing.transform(-1f)
        val expected = 0f

        assertEquals(expected, actual)
    }

    @Test
    fun WHEN_fraction_0_THEN_easing_invoked_with_0() {
        val actual = mappedEasing.transform(0f)
        val expected = 0f

        assertEquals(expected, actual)
    }

    @Test
    fun WHEN_fraction_less_than_min_THEN_easing_invoked_with_0() {
        val actual = mappedEasing.transform(lt_min)
        val expected = 0f

        assertEquals(expected, actual)
    }

    @Test
    fun WHEN_fraction_equals_min_THEN_easing_invoked_with_0() {
        val actual = mappedEasing.transform(min)
        val expected = 0f

        assertEquals(expected, actual)
    }

    @Test
    fun WHEN_fraction_between_min_max_THEN_easing_invoked_with_mapped_value() {
        val actual = mappedEasing.transform(halfway_min_max)
        val expected = 0.5f
        val diff = abs(actual - expected)
        val tolerance = 0.0001f

        assertTrue(diff < tolerance)
    }

    @Test
    fun WHEN_fraction_equals_max_THEN_easing_invoked_with_1f() {
        val actual = mappedEasing.transform(max)
        val expected = 1f

        assertEquals(expected, actual)
    }

    @Test
    fun WHEN_fraction_greater_than_max_THEN_easing_invoked_with_1f() {
        val actual = mappedEasing.transform(gt_max)
        val expected = 1f

        assertEquals(expected, actual)
    }

    @Test
    fun WHEN_fraction_greater_than_1_THEN_easing_invoked_with_1f() {
        val actual = mappedEasing.transform(2f)
        val expected = 1f

        assertEquals(expected, actual)
    }
}
