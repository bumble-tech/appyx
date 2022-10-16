package com.bumble.appyx.core.navigation2.inputsource

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.ui.geometry.Offset
import com.bumble.appyx.core.navigation2.NavModel
import com.bumble.appyx.core.navigation2.Operation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
                Log.d("input source", "operation applied: $operation")
            } else {
                Log.d("input source", "operation not applicable: $operation")
            }
            // Case: we can continue the existing operation
        }

        val startProgress = gesture!!.startProgress!!

        // Case: we go forward, it's cool
        if (totalTarget > startProgress) {

            // Case: standard forward progress
            if (totalTarget < startProgress + 1) {
                navModel.setProgress(totalTarget)
                Log.d("input source", "delta applied forward, new progress: ${navModel.currentProgress}")

            // Case: target is beyond the current segment, we'll need a new operation
            } else {
                // TODO without recursion
                val remainder = consumePartial(dragAmount, totalTarget, deltaProgress, startProgress + 1)
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

    private fun consumePartial(dragAmount: Offset, totalTarget: Float, deltaProgress: Float, boundary: Float): Offset {
        navModel.setProgress(boundary)
        navModel.dropAfter(boundary.toInt())
        val remainder = gesture!!.partial(dragAmount, totalTarget - (boundary))
        gesture = null
        Log.d("input source", "1 ------")
        Log.d("input source", "initial offset was: $dragAmount")
        Log.d("input source", "initial deltaProgress was: $deltaProgress")
        Log.d("input source", "initial target was: $totalTarget, beyond current segment: $boundary")
        Log.d("input source", "remainder progress: ${totalTarget - boundary}")
        Log.d("input source", "remainder offset: $remainder")
        Log.d("input source", "going back to start, reevaluate")
        Log.d("input source", "2 ------")
        // TODO without recursion
        return remainder
    }

    fun settle() {
        val targetValue = navModel.currentProgress.roundToInt()
        Log.d("input source", "Settle ${navModel.currentProgress} to: $targetValue")
        coroutineScope.launch {
            animatable.snapTo(navModel.currentProgress)
            // TODO animation spec similarly to AnimatedInputSource / default
            result = animatable.animateTo(targetValue.toFloat(), spring()) {
                navModel.setProgress(this.value)
            }
            navModel.dropAfter(targetValue)
        }
    }
}
