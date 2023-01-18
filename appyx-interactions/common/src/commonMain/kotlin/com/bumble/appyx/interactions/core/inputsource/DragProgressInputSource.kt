package com.bumble.appyx.interactions.core.inputsource

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.GestureFactory

class DragProgressInputSource<NavTarget : Any, State>(
    private val model: TransitionModel<NavTarget, State>,
    private val gestureFactory: GestureFactory<NavTarget, State>
) : Draggable {

    // TODO get rid of this
    private var _gestureFactory: ((Offset) -> Gesture<NavTarget, State>)? = null
        set(value) {
            field = value
            if (value == null) {
                gesture = null
            }
        }

    private var gesture: Gesture<NavTarget, State>? = null

    override fun onDrag(dragAmount: Offset, density: Density) {
        gestureFactory.createGesture(dragAmount, density)
        if (_gestureFactory == null) {
            _gestureFactory = { dragAmount ->
                gestureFactory.createGesture(dragAmount, density)
            }
        }
        consumeDrag(dragAmount)
    }

    override fun onDragEnd() {
        _gestureFactory = null
    }

    private fun consumeDrag(dragAmount: Offset) {
        requireNotNull(_gestureFactory)
        if (gesture == null) {
            gesture = _gestureFactory!!.invoke(dragAmount)
        }

        requireNotNull(gesture)
        val operation = gesture!!.operation
        val deltaProgress = gesture!!.dragToProgress(dragAmount)
        val currentProgress = model.currentProgress
        val totalTarget = currentProgress + deltaProgress

        // Case: we can start a new operation
        if (gesture!!.startProgress == null) {
            if (model.enqueue(operation)) {
                gesture!!.startProgress = currentProgress
                /* FIXME Logger */ println("operation applied: $operation")
            } else {
                /* FIXME Logger */ println("operation not applicable: $operation")
                return
            }
            // Case: we can continue the existing operation
        }

        val startProgress = gesture!!.startProgress!!

        // Case: we go forward, it's cool
        if (totalTarget > startProgress) {

            // Case: standard forward progress
            if (totalTarget < startProgress + 1) {
                model.setProgress(totalTarget)
                /* FIXME Logger */ println(
                    "delta applied forward, new progress: ${model.currentProgress}"
                )

                // Case: target is beyond the current segment, we'll need a new operation
            } else {
                // TODO without recursion
                val remainder =
                    consumePartial(dragAmount, totalTarget, deltaProgress, startProgress + 1)
                consumeDrag(remainder)
            }

            // Case: we went back to or beyond the start,
            // now we need to re-evaluate for a new operation
        } else {
            // TODO without recursion
            val remainder = consumePartial(dragAmount, totalTarget, deltaProgress, startProgress)
            consumeDrag(remainder)
        }
    }

    private fun consumePartial(
        dragAmount: Offset,
        totalTarget: Float,
        deltaProgress: Float,
        boundary: Float
    ): Offset {
        model.setProgress(boundary)
        model.dropAfter(boundary.toInt())
        val remainder = gesture!!.partial(dragAmount, totalTarget - (boundary))
        gesture = null
        /* FIXME Logger */ println("1 ------")
        /* FIXME Logger */ println("initial offset was: $dragAmount")
        /* FIXME Logger */ println("initial deltaProgress was: $deltaProgress")
        /* FIXME Logger */ println("initial target was: $totalTarget, beyond current segment: $boundary")
        /* FIXME Logger */ println("remainder progress: ${totalTarget - boundary}")
        /* FIXME Logger */ println("remainder offset: $remainder")
        /* FIXME Logger */ println("going back to start, reevaluate")
        /* FIXME Logger */ println("2 ------")
        // TODO without recursion
        return remainder
    }
}
