package com.bumble.appyx.interactions.core.inputsource

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.Operation.Mode.GEOMETRY
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.Operation.Mode.KEYFRAME
import com.bumble.appyx.interactions.core.Operation.Mode.IMMEDIATE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor

class AnimatedInputSource<NavTarget : Any, ModelState>(
    private val model: TransitionModel<NavTarget, ModelState>,
    private val coroutineScope: CoroutineScope,
    private val defaultAnimationSpec: AnimationSpec<Float> = spring()
) : InputSource<NavTarget, ModelState> {

    private val animatable = Animatable(0f)
    // FIXME private lateinit var result: AnimationResult<Float, AnimationVector1D>

    override fun operation(operation: Operation<ModelState>) {
        operation(operation, defaultAnimationSpec)
    }

    fun operation(
        operation: Operation<ModelState>,
        animationSpec: AnimationSpec<Float>
    ) {
        model.operation(operation)
        animateModel(
            target = model.maxProgress,
            animationSpec = animationSpec,
            cancelVelocity = false,
        )
    }

    fun settle(
        // FIXME @FloatRange(from = 0.0, to = 1.0)
        completionThreshold: Float = 0.5f,
        completeGestureSpec: AnimationSpec<Float> = spring(),
        revertGestureSpec: AnimationSpec<Float> = spring(),
    ) {
        val currentProgress = model.currentProgress
        val (targetValue, animationSpec) = if (currentProgress % 1 < completionThreshold) {
            floor(currentProgress).toInt() to revertGestureSpec
        } else {
            ceil(currentProgress).toInt() to completeGestureSpec
        }

        Logger.log(TAG, "Settle ${model.currentProgress} to: $targetValue")
        animateModel(
            target = targetValue.toFloat(),
            animationSpec = animationSpec,
            cancelVelocity = true,
            onAnimationFinished = {
                model.dropAfter(targetValue)
            }
        )
    }

    fun animateModel(
        target: Float,
        animationSpec: AnimationSpec<Float>,
        cancelVelocity: Boolean,
        onAnimationFinished: () -> Unit = {}
    ) {
        val velocity = animatable.velocity // snap will reset it to 0
        coroutineScope.launch(Dispatchers.Main) {
            animatable.snapTo(model.currentProgress)
            animatable.animateTo(
                targetValue = target,
                animationSpec = animationSpec,
                initialVelocity = if (cancelVelocity) 0f else velocity,
            ) {
                model.setProgress(progress = value)
            }
            onAnimationFinished()
        }
    }

    fun stopModel() {
        coroutineScope.launch(Dispatchers.Main) {
            animatable.snapTo(model.currentProgress)
        }
    }

    private companion object {
        private val TAG = AnimatedInputSource::class.java.name
    }
}
