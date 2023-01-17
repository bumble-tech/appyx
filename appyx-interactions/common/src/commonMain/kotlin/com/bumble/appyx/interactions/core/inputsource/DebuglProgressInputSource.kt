package com.bumble.appyx.interactions.core.inputsource

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.Operation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DebuglProgressInputSource<NavTarget, State>(
    private val navModel: BaseTransitionModel<NavTarget, State>,
    private val coroutineScope: CoroutineScope,
) : InputSource<NavTarget, State> {
    private val animatable = Animatable(0f)
    private lateinit var result: AnimationResult<Float, AnimationVector1D>
    private var progress: Float = 1f

    override fun operation(operation: Operation<NavTarget, State>) {
        navModel.enqueue(operation)
    }

    fun setNormalisedProgress(progress: Float) {
        this.progress = progress.coerceIn(0f, 1f)
        // TODO enforce min 1f in NavModel as a hidden detail rather than here:
        navModel.setProgress(1f + this.progress * (navModel.maxProgress - 1f))
    }

    fun settle() {
        Logger.log("input source", "Settle ${progress} to: ${progress.roundToInt().toFloat()}")
        coroutineScope.launch {
            animatable.snapTo(progress)
            result = animatable.animateTo(progress.roundToInt().toFloat(), spring()) {
                setNormalisedProgress(this.value)
            }
        }
    }
}
