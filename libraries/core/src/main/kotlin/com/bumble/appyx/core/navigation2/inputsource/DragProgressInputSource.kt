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
        val maxProgress = navModel.maxProgress

        if (deltaProgress > 1) {
            Log.w("input source", "This case could mean a different operation halfway, " +
                "but support for that is currently not implemented")
        }

        val totalTarget = currentProgress + deltaProgress

        // Case: we can start a new operation
        if (gesture!!.startProgress == null) {

            if (navModel.enqueue(operation)) {
                gesture!!.startProgress = currentProgress
                navModel.setProgress(totalTarget)
                Log.d("input source", "operation applied: $operation")
            } else {
                Log.d("input source", "operation not applicable: $operation")
            }
        // Case: we can continue the existing operation
        } else {
            val startProgress = gesture!!.startProgress!!

            // Case: we go forward, it's cool
            if (totalTarget > startProgress) {

                // We go beyond the current segment, we'll need a new operation
                if (totalTarget > startProgress + 1) {
                    navModel.setProgress(startProgress + 1)
//                    navModel.dropAfter(startProgress.toInt())
                    val remainder = gesture!!.partial(dragAmount, totalTarget - (startProgress + 1))
                    gesture = null
                    Log.d("input source", "1 ------")
                    Log.d("input source", "initial offset was: $dragAmount")
                    Log.d("input source", "initial deltaProgress was: $deltaProgress")
                    Log.d("input source", "initial target was: $totalTarget, beyond current segment: ${startProgress + 1}")
                    Log.d("input source", "remainder progress: ${totalTarget - (startProgress + 1)}")
                    Log.d("input source", "remainder offset: $remainder")
                    Log.d("input source", "going back to start, reevaluate")
                    Log.d("input source", "2 ------")
                    // TODO without recursion
                    addDeltaProgress(remainder)
                    // 0.291108575843523

//                    Log.d("input source", "beyond current segment")
//                    navModel.setProgress(startProgress + 1)
//                    gesture = null // TODO this shouldn't assume that the next operation will be applicable
//                    // TODO remove consumed part
//                    // TODO without recursion
//                    addDeltaProgress(dragAmount)

                // Case: standard forward progress
                } else {
                    navModel.setProgress(totalTarget)
                    Log.d("input source", "delta applied forward, new progress: ${navModel.currentProgress}")
                }

            // Case: we went back to or beyond the start,
            // now we need to re-evaluate for a new operation
            } else {
                navModel.setProgress(startProgress)
                navModel.dropAfter(startProgress.toInt())
                val remainder = gesture!!.partial(dragAmount, totalTarget - startProgress)
                gesture = null
                Log.d("input source", "1 ------")
                Log.d("input source", "initial offset was: $dragAmount")
                Log.d("input source", "initial deltaProgress was: $deltaProgress")
                Log.d("input source", "initial target was: $totalTarget, beyond current segment: $startProgress")
                Log.d("input source", "remainder progress: ${totalTarget - startProgress}")
                Log.d("input source", "remainder offset: $remainder")
                Log.d("input source", "going back to start, reevaluate")
                Log.d("input source", "2 ------")
                // TODO without recursion
                addDeltaProgress(remainder)
            }
        }
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
