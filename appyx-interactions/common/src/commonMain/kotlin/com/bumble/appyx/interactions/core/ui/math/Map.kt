package com.bumble.appyx.interactions.core.ui.helper


/**
 * Maps a value from a range of [min, max] to [destMin, destMax]
 */
fun mapFloat(v: Float, min: Float, max: Float, destMin: Float, destMax: Float): Float =
    lerpFloat(
        start = destMin,
        end = destMax,
        normFloat(
            v = v.coerceIn(min, max),
            min = min,
            max = max
        )
    )
