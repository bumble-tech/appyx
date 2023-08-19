package com.bumble.appyx.interactions.core.model.progress

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import com.bumble.appyx.interactions.core.model.transition.TransitionModel.SettleDirection.COMPLETE
import com.bumble.appyx.interactions.core.model.transition.TransitionModel.SettleDirection.REVERT
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.utils.multiplatform.AppyxLogger

internal class DragProgressController<InteractionTarget : Any, State>(
    private val model: TransitionModel<InteractionTarget, State>,
    private val gestureFactory: () -> GestureFactory<InteractionTarget, State>,
    override val defaultAnimationSpec: AnimationSpec<Float>,
) : Draggable {

    // TODO get rid of this
    private var _gestureFactory: ((Offset) -> Gesture<InteractionTarget, State>)? = null
        set(value) {
            field = value
            if (value == null) {
                gesture = null
            }
        }

    private var gesture: Gesture<InteractionTarget, State>? = null

    override fun onStartDrag(position: Offset) {
        gestureFactory().onStartDrag(position)
    }

    override fun onDrag(dragAmount: Offset, density: Density) {
        if (_gestureFactory == null) {
            _gestureFactory = { dragAmount ->
                gestureFactory().createGesture(
                    state = model.output.value.currentTargetState,
                    delta = dragAmount,
                    density = density
                )
            }
        }
        consumeDrag(dragAmount)
    }

    override fun onDragEnd() {
        _gestureFactory = null
    }

    fun isDragging(): Boolean = _gestureFactory != null

    private fun consumeDrag(dragAmount: Offset) {
        val currentState = model.output.value
        require(dragAmount.isValid()) { "dragAmount is NaN" }
        if (dragAmount.getDistanceSquared() == 0f) {
            return
        }
        requireNotNull(_gestureFactory) { "This should have been set already in this class" }
        if (gesture == null) {
            gesture = _gestureFactory!!.invoke(dragAmount)
        }

        requireNotNull(gesture)
        val operation = gesture!!.operation
        operation.mode = KEYFRAME
        val deltaProgress = gesture!!.dragToProgress(dragAmount)
        require(!deltaProgress.isNaN()) {
            "deltaProgress is NaN! – dragAmount: $dragAmount, gesture: $gesture, operation: $operation"
        }
        val currentProgress = if (currentState is Keyframes<*>) currentState.progress else 0f
        val totalTarget = currentProgress + deltaProgress

        // Case: we can start a new operation
        if (gesture!!.startProgress == null) {
            // TODO internally this will always apply it to the end of a Keyframes queue,
            //  which is not necessarily what we want:
            if (model.canApply(operation)) {
                model.operation(operation)
                gesture!!.startProgress = currentProgress
                AppyxLogger.d(TAG, "Gesture operation applied: $operation")
            } else {
                AppyxLogger.d(TAG, "Gesture operation wasn't applied, releasing it to re-evaluate")
                gesture = null
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
                val currentProgress = if (currentState is Keyframes<*>) currentState.progress else 0f
                AppyxLogger.d(
                    TAG,
                    "delta applied forward, new progress: $currentProgress"
                )

                // Case: target is beyond the current segment, we'll need a new operation
            } else {
                // TODO without recursion
                val remainder =
                    consumePartial(COMPLETE, dragAmount, totalTarget, deltaProgress, startProgress + 1)
                if (remainder.getDistanceSquared() > 0) {
                    consumeDrag(remainder)
                }
            }

            // Case: we went back to or beyond the start,
            // now we need to re-evaluate for a new operation
        } else {
            // TODO without recursion
            val remainder = consumePartial(REVERT, dragAmount, totalTarget, deltaProgress, startProgress)
            if (dragAmount != remainder) {
                consumeDrag(remainder)
            }
        }
    }

    private fun consumePartial(
        direction: TransitionModel.SettleDirection,
        dragAmount: Offset,
        totalTarget: Float,
        deltaProgress: Float,
        boundary: Float
    ): Offset {
        model.setProgress(boundary)
        model.onSettled(direction, false)
        val remainder = gesture!!.partial(dragAmount, totalTarget - (boundary))
        gesture = null
        AppyxLogger.d(TAG, "1 ------")
        AppyxLogger.d(TAG, "initial offset was: $dragAmount")
        AppyxLogger.d(TAG, "initial deltaProgress was: $deltaProgress")
        AppyxLogger.d(TAG, "initial target was: $totalTarget, beyond current segment: $boundary")
        AppyxLogger.d(TAG, "remainder progress: ${totalTarget - boundary}")
        AppyxLogger.d(TAG, "remainder offset: $remainder")
        AppyxLogger.d(TAG, "going back to start, reevaluate")
        AppyxLogger.d(TAG, "2 ------")
        // TODO without recursion
        return remainder
    }

    private companion object {
        private const val TAG = "DragProgressController"
    }
}
