package com.bumble.appyx.interactions.core.model.progress

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.core.model.transition.Keyframes
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import com.bumble.appyx.interactions.core.model.transition.TransitionModel.SettleDirection.COMPLETE
import com.bumble.appyx.interactions.core.model.transition.TransitionModel.SettleDirection.REVERT
import com.bumble.appyx.utils.multiplatform.AppyxLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor

class AnimatedProgressController<InteractionTarget : Any, ModelState>(
    private val model: TransitionModel<InteractionTarget, ModelState>,
    private val coroutineScope: CoroutineScope,
    override val defaultAnimationSpec: AnimationSpec<Float> = spring(),
    private val animateSettle: Boolean = false
) : ProgressController<InteractionTarget, ModelState>, HasDefaultAnimationSpec<Float> {

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
        val currentState = model.output.value
        if (currentState is Keyframes<ModelState>) {
            animateModel(
                from = { currentState.progress },
                target = { currentState.maxProgress },
                animationSpec = animationSpec,
                cancelVelocity = false,
                onAnimationFinished = {
                    model.onSettled(
                        direction = COMPLETE,
                        animate = animateSettle
                    )
                }
            )
        }
    }

    fun settle(
        // FIXME @FloatRange(from = 0.0, to = 1.0)
        completionThreshold: Float = 0.5f,
        completeGestureSpec: AnimationSpec<Float> = spring(),
        revertGestureSpec: AnimationSpec<Float> = spring(),
    ) {
        val currentState = model.output.value
        if (currentState is Keyframes<ModelState>) {
            val currentProgress = currentState.progress
            val direction: TransitionModel.SettleDirection = if (currentProgress % 1 < completionThreshold) REVERT else COMPLETE
            val targetValue = if (direction == REVERT) floor(currentProgress).toInt() else ceil(currentProgress).toInt()
            val animationSpec = if (direction == REVERT) revertGestureSpec else completeGestureSpec

            AppyxLogger.d(TAG, "Settle ${currentState.progress} to: $targetValue")
            animateModel(
                from = { currentState.progress },
                target = { targetValue.toFloat() },
                animationSpec = animationSpec,
                cancelVelocity = true,
                onAnimationFinished = {
                    model.onSettled(
                        direction = direction,
                        animate = animateSettle
                    )
                }
            )
        }
    }

    private fun animateModel(
        from: () -> Float,
        target: () -> Float,
        animationSpec: AnimationSpec<Float>,
        cancelVelocity: Boolean,
        onAnimationFinished: () -> Unit = {}
    ) {
        val velocity = animatable.velocity // snap will reset it to 0
        coroutineScope.launch(Dispatchers.Main.immediate) {
            animatable.snapTo(from())
            animatable.animateTo(
                targetValue = target(),
                animationSpec = animationSpec,
                initialVelocity = if (cancelVelocity) 0f else velocity,
            ) {
                model.setProgress(progress = value)
            }
            onAnimationFinished()
        }
    }

    fun stopModel() {
        val currentState = model.output.value
        if (currentState is Keyframes<ModelState>) {
            coroutineScope.launch(Dispatchers.Main) {
                animatable.snapTo(currentState.progress)
            }
        }
    }

    private companion object {
        private const val TAG = "AnimatedProgressController"
    }
}
