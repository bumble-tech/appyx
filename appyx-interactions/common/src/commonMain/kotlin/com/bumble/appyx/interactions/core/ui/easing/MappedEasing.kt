package com.bumble.appyx.interactions.core.ui.easing

import androidx.compose.animation.core.Easing
import com.bumble.appyx.interactions.core.ui.helper.mapFloat

/**
 * Expects two params such that: 0 <= min < max <= 1
 *
 * Delegates easing transform to the passed in easing, such that:
 * - In the range [0, min]   – easing will be called with 0
 * - In the range [min, max] - easing will be called with [0, 1] mapped from this range
 * - In the range [max, 1]   - easing will be called with 1
 */
class MappedEasing(
    private val min: Float,
    private val max: Float,
    private val easing: Easing
) : Easing {

    override fun transform(fraction: Float): Float =
        when {
            (0f..min).contains(fraction) -> easing.transform(0f)
            (min..max).contains(fraction) -> easing.transform(
                mapFloat(fraction, min, max, 0f, 1f)
            )
            else -> easing.transform(1f)
        }
}
