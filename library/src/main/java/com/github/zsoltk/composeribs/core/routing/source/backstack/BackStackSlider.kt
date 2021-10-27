package com.github.zsoltk.composeribs.core.routing.source.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionSpec
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler

@Suppress("TransitionPropertiesLabel")
class BackStackSlider(
    private val transitionSpec: TransitionSpec<BackStack.TransitionState, Offset> = { tween(1500) },
    override val clipToBounds: Boolean = false
) : UpdateTransitionHandler<BackStack.TransitionState>() {

    @Composable
    override fun map(
        transition: Transition<BackStack.TransitionState>,
        transitionBounds: TransitionBounds
    ): Modifier {
        val offset = transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                val width = transitionBounds.width.value
                when (it) {
                    BackStack.TransitionState.CREATED -> Offset(1.0f * width, 0f)
                    BackStack.TransitionState.ON_SCREEN -> Offset(0f, 0f)
                    BackStack.TransitionState.STASHED_IN_BACK_STACK -> Offset(-1.0f * width, 0f)
                    BackStack.TransitionState.DESTROYED -> Offset(1.0f * width, 0f)
                }
            })


        return Modifier.offset(Dp(offset.value.x), Dp(offset.value.y))

    }
}
