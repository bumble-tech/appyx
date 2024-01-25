package com.bumble.appyx.interactions.gesture

import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock1
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock10
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock11
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock12
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock2
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock3
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock4
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock5
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock6
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock7
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock8
import com.bumble.appyx.interactions.gesture.Drag.ClockDirection.Clock9
import com.bumble.appyx.interactions.gesture.Drag.Direction4
import com.bumble.appyx.interactions.gesture.Drag.Direction8
import com.bumble.appyx.interactions.gesture.Drag.Direction8.DOWNLEFT
import com.bumble.appyx.interactions.gesture.Drag.Direction8.DOWNRIGHT
import com.bumble.appyx.interactions.gesture.Drag.Direction8.UPLEFT
import com.bumble.appyx.interactions.gesture.Drag.Direction8.UPRIGHT
import com.bumble.appyx.interactions.gesture.Drag.HorizontalDirection
import com.bumble.appyx.interactions.gesture.Drag.HorizontalDirection.LEFT
import com.bumble.appyx.interactions.gesture.Drag.HorizontalDirection.RIGHT
import com.bumble.appyx.interactions.gesture.Drag.VerticalDirection
import com.bumble.appyx.interactions.gesture.Drag.VerticalDirection.DOWN
import com.bumble.appyx.interactions.gesture.Drag.VerticalDirection.UP
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

    @Suppress("MagicNumber")
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
fun dragHorizontalDirection(delta: Offset): HorizontalDirection =
    if (delta.x < 0) LEFT else RIGHT

/**
 * The vertical aspect of the drag (UP or DOWN), regardless of the dominant direction
 */
fun dragVerticalDirection(delta: Offset): VerticalDirection =
    if (delta.y < 0) UP else DOWN

/**
 * The dominant direction of the drag of 4 possible directions
 */
fun dragDirection4(delta: Offset) =
    if (abs(delta.x) > abs(delta.y)) {
        if (delta.x < 0) Direction4.LEFT else Direction4.RIGHT
    } else {
        if (delta.y < 0) Direction4.UP else Direction4.DOWN
    }

/**
 * The dominant direction of the drag of 8 possible directions
 */
@Suppress("MagicNumber")
fun dragDirection8(delta: Offset): Direction8 {
    val angle = dragAngleDegrees(delta)
    return when {
        (0.0..22.5).contains(angle) -> Direction8.UP
        (22.5..67.5).contains(angle) -> UPRIGHT
        (67.5..112.5).contains(angle) -> Direction8.RIGHT
        (112.5..157.5).contains(angle) -> DOWNRIGHT
        (157.5..202.5).contains(angle) -> Direction8.DOWN
        (202.5..247.5).contains(angle) -> DOWNLEFT
        (247.5..292.5).contains(angle) -> Direction8.LEFT
        (292.5..337.5).contains(angle) -> UPLEFT
        else -> Direction8.UP
    }
}

/**
 * The drag direction interpreted on the clock
 */
@Suppress("MagicNumber")
fun dragClockDirection(delta: Offset): ClockDirection {
    val angle = dragAngleDegrees(delta)
    return when {
        (0.0..15.0).contains(angle) -> Clock12
        (15.0..45.0).contains(angle) -> Clock1
        (45.0..75.0).contains(angle) -> Clock2
        (75.0..105.0).contains(angle) -> Clock3
        (105.0..135.0).contains(angle) -> Clock4
        (135.0..165.0).contains(angle) -> Clock5
        (165.0..195.0).contains(angle) -> Clock6
        (195.0..225.0).contains(angle) -> Clock7
        (225.0..255.0).contains(angle) -> Clock8
        (255.0..285.0).contains(angle) -> Clock9
        (285.0..315.0).contains(angle) -> Clock10
        (315.0..345.0).contains(angle) -> Clock11
        else -> Clock12
    }
}

