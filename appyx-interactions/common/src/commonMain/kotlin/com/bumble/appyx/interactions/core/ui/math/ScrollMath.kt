package com.bumble.appyx.interactions.core.ui.math

import com.bumble.appyx.interactions.core.ui.property.impl.Scale
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

fun scaleUpTo(
    v: Float,
    scale: Scale.Target.Scale,
    slope: Float
) =
    Scale.Target.Scale(
        scaleX = (slope * scale.scaleX * v).coerceIn(-abs(scale.scaleX), abs(scale.scaleX)),
        scaleY = (slope * scale.scaleY * v).coerceIn(-abs(scale.scaleY), abs(scale.scaleY))
    )

