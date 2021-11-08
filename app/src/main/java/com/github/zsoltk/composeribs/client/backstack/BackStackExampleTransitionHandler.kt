package com.github.zsoltk.composeribs.client.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackFader
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Replace
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler

class BackStackExampleTransitionHandler<T> :
    UpdateTransitionHandler<T, BackStack.TransitionState>(clipToBounds = true) {

    private val slider = BackStackSlider<T>(clipToBounds = clipToBounds)
    private val fader = BackStackFader<T>()

    @Composable
    override fun map(
        transition: Transition<BackStack.TransitionState>,
        descriptor: TransitionDescriptor<T, BackStack.TransitionState>
    ): Modifier =
        when (descriptor.operation) {
            is Replace -> fader.map(transition, descriptor)
            else -> slider.map(transition, descriptor)
        }
}
