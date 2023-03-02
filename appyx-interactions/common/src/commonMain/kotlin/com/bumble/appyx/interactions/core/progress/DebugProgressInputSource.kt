package com.bumble.appyx.interactions.core.progress

import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.TransitionModel
import kotlinx.coroutines.CoroutineScope

// FIXME
class DebugProgressInputSource<NavTarget, ModelState>(
    private val transitionModel: TransitionModel<NavTarget, ModelState>,
    private val coroutineScope: CoroutineScope,
) : ProgressController<NavTarget, ModelState> {
    // TODO this should >not< use its own animatable that's independent of AnimatedInputSource
//    private val animatable = Animatable(0f)
    private lateinit var result: AnimationResult<Float, AnimationVector1D>
    private var progress: Float = 1f

    override fun operation(operation: Operation<ModelState>) {
        // Regardless of operation.mode, only enqueue makes sense
        transitionModel.operation(operation, Operation.Mode.KEYFRAME)
    }

    fun setNormalisedProgress(progress: Float) {
        // FIXME
//        this.progress = progress.coerceIn(0f, 1f)
//        // TODO enforce min 1f in NavModel as a hidden detail rather than here:
//        transitionModel.setProgress(1f + this.progress * (transitionModel.maxProgress - 1f))
    }

    fun settle() {
//        Logger.log(TAG, "Settle ${progress} to: ${progress.roundToInt().toFloat()}")
//        coroutineScope.launch {
//            animatable.snapTo(progress)
//            result = animatable.animateTo(progress.roundToInt().toFloat(), spring()) {
//                setNormalisedProgress(this.value)
//            }
//        }
    }

    fun stopModel() {
//        coroutineScope.launch(Dispatchers.Main) {
//            animatable.snapTo(transitionModel.currentProgress)
//        }
    }

    private companion object {
        private val TAG = DebugProgressInputSource::class.java.name
    }
}