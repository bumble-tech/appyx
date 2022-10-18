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

class AnimatedInputSource<NavTarget : Any, State>(
    private val navModel: BaseNavModel<NavTarget, State>,
    private val coroutineScope: CoroutineScope,
    private val defaultAnimationSpec: AnimationSpec<Float> = spring()
) : InputSource<NavTarget, State> {
    private val animatable = Animatable(0f)
    private lateinit var result: AnimationResult<Float, AnimationVector1D>

    override fun operation(operation: Operation<NavTarget, State>) {
        operation(operation, defaultAnimationSpec)
    }

    fun operation(operation: Operation<NavTarget, State>, animationSpec: AnimationSpec<Float> = defaultAnimationSpec) {
        navModel.enqueue(operation)

        coroutineScope.launch {
            result = animatable.animateTo(navModel.maxProgress, animationSpec) {
                navModel.setProgress(progress = this.value)
            }
        }
    }
}
