package com.bumble.appyx.interactions.core.inputsource

import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.NavModel
import com.bumble.appyx.interactions.core.Operation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor

class DragProgressInputSource<NavTarget, State>(
    private val navModel: NavModel<NavTarget, State>,
    private val coroutineScope: CoroutineScope,
) : InputSource<NavTarget, State> {
    private val animatable = Animatable(0f)
    private lateinit var result: AnimationResult<Float, AnimationVector1D>

    var gestureFactory: ((Offset) -> Gesture<NavTarget, State>)? = null
        set(value) {
            field = value
            if (value == null) {
                gesture = null
            }
        }

    private var gesture: Gesture<NavTarget, State>? = null

    override fun operation(operation: Operation<NavTarget, State>) {
        navModel.enqueue(operation)
    }

    fun addDeltaProgress(dragAmount: Offset) {
        requireNotNull(gestureFactory)
        if (gesture == null) {
            gesture = gestureFactory!!.invoke(dragAmount)
        }

        requireNotNull(gesture)
        val operation = gesture!!.operation
        val deltaProgress = gesture!!.dragToProgress(dragAmount)
        val currentProgress = navModel.currentProgress
        val totalTarget = currentProgress + deltaProgress

        // Case: we can start a new operation
        if (gesture!!.startProgress == null) {
            if (navModel.enqueue(operation)) {
                gesture!!.startProgress = currentProgress
                Logger.log("input source", "operation applied: $operation")
            } else {
                Logger.log("input source", "operation not applicable: $operation")
                return
            }
            // Case: we can continue the existing operation
        }

        val startProgress = gesture!!.startProgress!!

        // Case: we go forward, it's cool
        if (totalTarget > startProgress) {

            // Case: standard forward progress
            if (totalTarget < startProgress + 1) {
                navModel.setProgress(totalTarget)
                Logger.log(
                    "input source",
                    "delta applied forward, new progress: ${navModel.currentProgress}"
                )

                // Case: target is beyond the current segment, we'll need a new operation
            } else {
                // TODO without recursion
                val remainder =
                    consumePartial(dragAmount, totalTarget, deltaProgress, startProgress + 1)
                addDeltaProgress(remainder)
            }

            // Case: we went back to or beyond the start,
            // now we need to re-evaluate for a new operation
        } else {
            // TODO without recursion
            val remainder = consumePartial(dragAmount, totalTarget, deltaProgress, startProgress)
            addDeltaProgress(remainder)
        }
    }

    private fun consumePartial(
        dragAmount: Offset,
        totalTarget: Float,
        deltaProgress: Float,
        boundary: Float
    ): Offset {
        navModel.setProgress(boundary)
        navModel.dropAfter(boundary.toInt())
        val remainder = gesture!!.partial(dragAmount, totalTarget - (boundary))
        gesture = null
        Logger.log("input source", "1 ------")
        Logger.log("input source", "initial offset was: $dragAmount")
        Logger.log("input source", "initial deltaProgress was: $deltaProgress")
        Logger.log(
            "input source",
            "initial target was: $totalTarget, beyond current segment: $boundary"
        )
        Logger.log("input source", "remainder progress: ${totalTarget - boundary}")
        Logger.log("input source", "remainder offset: $remainder")
        Logger.log("input source", "going back to start, reevaluate")
        Logger.log("input source", "2 ------")
        // TODO without recursion
        return remainder
    }

    fun settle(
        // FIXME @FloatRange(from = 0.0, to = 1.0)
        roundUpThreshold: Float = 0.5f,
        roundUpAnimationSpec: AnimationSpec<Float> = spring(),
        roundDownAnimationSpec: AnimationSpec<Float> = spring(),
    ) {
        val currentProgress = navModel.currentProgress
        val (animationSpec, targetValue) = if (currentProgress % 1 < roundUpThreshold) {
            roundDownAnimationSpec to floor(currentProgress).toInt()
        } else {
            roundUpAnimationSpec to ceil(currentProgress).toInt()
        }
        Logger.log("input source", "Settle ${navModel.currentProgress} to: $targetValue")
        coroutineScope.launch {
            animatable.snapTo(navModel.currentProgress)
            // TODO animation spec similarly to AnimatedInputSource / default
            result = animatable.animateTo(targetValue.toFloat(), animationSpec) {
                navModel.setProgress(this.value)
            }
            navModel.dropAfter(targetValue)
        }
    }
}
