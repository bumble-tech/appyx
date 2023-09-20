package com.bumble.appyx.interactions.core.gesture

import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.positionChange

/**
 * A more specific version of `detectDragGestures` where gesture can be cancelled
 * based on user needs. `onDrag` lambda should return true to keep consuming gesture
 * or false to cancel it and wait for next gesture to start.
 */
suspend fun PointerInputScope.detectDragGesturesOrCancellation(
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Boolean
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var drag: PointerInputChange?
        var overSlop = Offset.Zero
        do {
            drag = awaitTouchSlopOrCancellation(
                down.id,
            ) { change, over ->
                change.consume()
                overSlop = over
            }
        } while (drag != null && !drag.isConsumed)
        if (drag != null) {
            onDragStart.invoke(drag.position)
            onDrag(drag, overSlop)
            if (
                drag(drag.id) {
                    onDrag(it, it.positionChange()).run {
                        it.consume()
                        this
                    }
                }
            ) {
                onDragEnd()
            }
        }
    }
}

@Suppress("ReturnCount")
private suspend fun AwaitPointerEventScope.drag(
    pointerId: PointerId,
    onDrag: (PointerInputChange) -> Boolean
): Boolean {
    var pointer = pointerId
    while (true) {
        val change = awaitDragOrCancellation(pointer) ?: return false

        if (change.changedToUpIgnoreConsumed()) {
            return true
        }

        if (!onDrag(change)) {
            return false
        }
        pointer = change.id
    }
}
