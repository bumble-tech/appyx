package com.bumble.appyx.interactions.core.ui.math

import kotlin.math.abs
import kotlin.math.sign

/**
 * Will result in a graph like:
 *
 *  \     /
 *   \___/
 *
 * Where the length of ___ = 2 x halfCenterRange
 */
fun cutOffCenter(ownValue: Float, movingValue: Float, halfCenterRange: Float) =
    (abs(ownValue - movingValue) - halfCenterRange).coerceAtLeast(0f)

/**
 * Will result in a graph like:
 *
 *        /
 *    ___/
 *  /
 *
 * Where the length of ___ = 2 x halfCenterRange
 */
fun cutOffCenterSigned(ownValue: Float, movingValue: Float, halfCenterRange: Float) =
    cutOffCenter(ownValue, movingValue, halfCenterRange) * sign(ownValue - movingValue)

fun scaleUpTo(v: Float, scale: Float, slope: Float) =
    (slope * scale * v).coerceIn(-abs(scale), abs(scale))


/**
 * Returns the value of x constrained to the range minVal to maxVal. The returned value is computed
 * x.coerceIn(min, max).
 *
 * @param x Specify the value to constrain.
 * @param minVal Specify the lower end of the range into which to constrain x.
 * @param maxVal Specify the upper end of the range into which to constrain x.
 *
 * Based on the OpenGL Shading Language clamp function.
 * https://registry.khronos.org/OpenGL-Refpages/gl4/html/clamp.xhtml
 */
fun clamp(x: Float, min: Float, max: Float): Float =
    x.coerceIn(min, max)

/**
 * Performs smooth Hermite interpolation between 0 and 1 when edge0 < x < edge1. This is useful in
 * cases where a threshold function with a smooth transition is desired.
 *
 * @param edge0 Specifies the value of the lower edge of the Hermite function.
 * @param edge1 Specifies the value of the upper edge of the Hermite function.
 * @param x Specifies the source value for interpolation.
 *
 * Based on the OpenGL Shading Language smoothstep function.
 * https://registry.khronos.org/OpenGL-Refpages/gl4/html/smoothstep.xhtml
 */
fun smoothstep(edge0: Float, edge1: Float, x: Float): Float =
    clamp((x - edge0) / (edge1 - edge0), 0f, 1f).run {
        this * this * (3f - 2f * this)
    }
