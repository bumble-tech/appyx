package com.bumble.appyx.core.navigation2.inputsource

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import com.bumble.appyx.core.navigation2.BaseNavModel
import com.bumble.appyx.core.navigation2.Operation
import kotlinx.coroutines.CoroutineScope

class ManualProgressInputSource<NavTarget, State>(
    private val navModel: BaseNavModel<NavTarget, State>,
    private val coroutineScope: CoroutineScope,
) : InputSource<NavTarget, State> {
    private val animatable = Animatable(0f)
    private lateinit var result: AnimationResult<Float, AnimationVector1D>

    override fun operation(operation: Operation<NavTarget, State>) {
        navModel.enqueue(operation)
    }

    fun setProgress(progress: Float) {
        // TODO
        navModel.setProgress(1f + progress.coerceIn(0f, 1f) * (navModel.maxProgress - 1f))
    }
}
