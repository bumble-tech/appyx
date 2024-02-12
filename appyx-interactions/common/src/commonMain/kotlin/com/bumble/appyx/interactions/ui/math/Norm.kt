package com.bumble.appyx.interactions.ui.math


/**
 * Normalises a value.
 *
 * A value on the range of expected [min, max] values will be mapped to [0, 1]
 */
fun normFloat(v: Float, min: Float, max: Float): Float =
    (v - min) / (max - min)
