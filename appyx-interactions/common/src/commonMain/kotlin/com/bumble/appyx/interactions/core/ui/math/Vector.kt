package com.bumble.appyx.interactions.core.ui.math

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos

/**
 * The angle of the vector such that:
 *
 * - 12 o'clock = 0 degrees
 * - 3 o'clock = 90 degrees
 */
fun angleDegrees(vector: Offset): Double  {
    val (x, y) = vector
    val deg = when {
        x == 0f -> when {
            y > 0 -> 90.0
            y < 0 -> 270.0
            else -> 0.0
        }
        y == 0f -> when {
            x > 0 -> 0.0
            x < 0 -> 180.0
            else -> 0.0
        }
        else -> {
            val deg = atan(y / x) * 180.0 / PI
            when {
                x < 0 -> 180 + deg
                y < 0 -> 360 + deg
                else -> deg
            }
        }
    }

    return (deg + 90) % 360
}

fun scalarComponentOf(v1: Offset, v2: Offset): Float {
    val theta = angleDegrees(v1) - angleDegrees(v2)
    val l = v1.getDistance()

    return (l * cos(theta / 180 * PI)).toFloat()
}

fun proportionOf(v1: Offset, v2: Offset): Float {
    val s = scalarComponentOf(v1, v2)

    return s / v2.getDistance()
}
