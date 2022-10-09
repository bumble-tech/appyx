package com.bumble.appyx.core.navigation2.inputsource

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import com.bumble.appyx.core.navigation2.BaseNavModel
import com.bumble.appyx.core.navigation2.Operation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ManualProgressInputSource<NavTarget, State>(
    private val navModel: BaseNavModel<NavTarget, State>,
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

    fun addDeltaProgress(delta: Float) {
        // FIXME this shouldn't indirectly delta towards maxProgress, only the next segment
        setNormalisedProgress(progress + delta)
    }

    fun settle() {
        Log.d("input source", "Settle ${progress} to: ${progress.roundToInt().toFloat()}")
        coroutineScope.launch {
            animatable.snapTo(progress)
            result = animatable.animateTo(progress.roundToInt().toFloat(), spring()) {
                setNormalisedProgress(this.value)
            }
        }
    }
}