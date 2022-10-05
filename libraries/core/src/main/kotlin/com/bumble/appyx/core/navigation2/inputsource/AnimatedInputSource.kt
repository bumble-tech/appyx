package com.bumble.appyx.core.navigation2.inputsource

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import com.bumble.appyx.core.navigation2.Operation
import com.bumble.appyx.core.navigation2.BaseNavModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AnimatedInputSource<Target : Any, State>(
    private val navModel: BaseNavModel<Target, State>,
    private val coroutineScope: CoroutineScope,
) {
    private val animatable = Animatable(0f)
    private lateinit var result: AnimationResult<Float, AnimationVector1D>

    fun operation(operation: Operation<Target, State>, animationSpec: AnimationSpec<Float> = spring()) {
        navModel.enqueue(operation)

        coroutineScope.launch {
            result = animatable.animateTo(navModel.maxProgress, animationSpec) {
                navModel.setProgress(progress = this.value)
            }
        }
    }
}
