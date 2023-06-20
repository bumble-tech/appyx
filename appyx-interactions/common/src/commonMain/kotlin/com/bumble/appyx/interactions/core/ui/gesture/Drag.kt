package com.bumble.appyx.interactions.core.ui.gesture

import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.interactions.core.ui.math.angleDegrees
import kotlin.math.abs

interface Drag {

    enum class VerticalDirection {
        UP, DOWN
    }

    enum class HorizontalDirection {
        LEFT, RIGHT
    }

    enum class Direction4 {
        UP, DOWN, LEFT, RIGHT
    }

    enum class Direction8 {
        UP, UPRIGHT, RIGHT, DOWNRIGHT, DOWN, DOWNLEFT, LEFT, UPLEFT
    }

    enum class ClockDirection(val digit: Int) {
        Clock1(1),
        Clock2(2),
        Clock3(3),
        Clock4(4),
        Clock5(5),
        Clock6(6),
        Clock7(7),
        Clock8(8),
        Clock9(9),
        Clock10(10),
        Clock11(11),
        Clock12(12)
    }
}

/**
 * The angle of the drag such that:
 *
 * - 12 o'clock = 0 degrees
 * - 3 o'clock = 90 degrees
 */
fun dragAngleDegrees(delta: Offset): Float  =
    angleDegrees(delta)

/**
 * The horizontal aspect of the drag (LEFT or RIGHT), regardless of the dominant direction
 */
fun dragHorizontalDirection(delta: Offset): Drag.HorizontalDirection =
    if (delta.x < 0) Drag.HorizontalDirection.LEFT else Drag.HorizontalDirection.RIGHT

/**
 * The vertical aspect of the drag (UP or DOWN), regardless of the dominant direction
 */
fun dragVerticalDirection(delta: Offset): Drag.VerticalDirection =
    if (delta.y < 0) Drag.VerticalDirection.UP else Drag.VerticalDirection.DOWN

/**
 * The dominant direction of the drag of 4 possible directions
 */
fun dragDirection4(delta: Offset) =
    if (abs(delta.x) > abs(delta.y)) {
        if (delta.x < 0) Drag.Direction4.LEFT else Drag.Direction4.RIGHT
    } else {
        if (delta.y < 0) Drag.Direction4.UP else Drag.Direction4.DOWN
    }

/**
 * The dominant direction of the drag of 8 possible directions
 */
fun dragDirection8(delta: Offset): Drag.Direction8 {
    val angle = dragAngleDegrees(delta)
    return when {
        (0.0..22.5).contains(angle) -> Drag.Direction8.UP
        (22.5..67.5).contains(angle) -> Drag.Direction8.UPRIGHT
        (67.5..112.5).contains(angle) -> Drag.Direction8.RIGHT
        (112.5..157.5).contains(angle) -> Drag.Direction8.DOWNRIGHT
        (157.5..202.5).contains(angle) -> Drag.Direction8.DOWN
        (202.5..247.5).contains(angle) -> Drag.Direction8.DOWNLEFT
        (247.5..292.5).contains(angle) -> Drag.Direction8.LEFT
        (292.5..337.5).contains(angle) -> Drag.Direction8.UPLEFT
        else -> Drag.Direction8.UP
    }
}

/**
 * The drag direction interpreted on the clock
 */
fun dragClockDirection(delta: Offset): Drag.ClockDirection {
    val angle = dragAngleDegrees(delta)
    return when {
        (0.0..15.0).contains(angle) -> Drag.ClockDirection.Clock12
        (15.0..45.0).contains(angle) -> Drag.ClockDirection.Clock1
        (45.0..75.0).contains(angle) -> Drag.ClockDirection.Clock2
        (75.0..105.0).contains(angle) -> Drag.ClockDirection.Clock3
        (105.0..135.0).contains(angle) -> Drag.ClockDirection.Clock4
        (135.0..165.0).contains(angle) -> Drag.ClockDirection.Clock5
        (165.0..195.0).contains(angle) -> Drag.ClockDirection.Clock6
        (195.0..225.0).contains(angle) -> Drag.ClockDirection.Clock7
        (225.0..255.0).contains(angle) -> Drag.ClockDirection.Clock8
        (255.0..285.0).contains(angle) -> Drag.ClockDirection.Clock9
        (285.0..315.0).contains(angle) -> Drag.ClockDirection.Clock10
        (315.0..345.0).contains(angle) -> Drag.ClockDirection.Clock11
        else -> Drag.ClockDirection.Clock12
    }
}

