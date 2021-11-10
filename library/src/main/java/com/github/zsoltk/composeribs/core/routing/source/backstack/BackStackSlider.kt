package com.github.zsoltk.composeribs.core.routing.source.backstack

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionSpec
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler

@Suppress("TransitionPropertiesLabel")
internal class BackStackSlider(
    private val transitionSpec: TransitionSpec<TransitionState, Offset>,
    override val clipToBounds: Boolean,
) : UpdateTransitionHandler<TransitionState>() {

    @Composable
    override fun map(
        transition: Transition<TransitionState>,
        transitionBounds: TransitionBounds
    ): Modifier {
        val offset = transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                val width = transitionBounds.width.value
                when (it) {
                    TransitionState.CREATED -> Offset(1.0f * width, 0f)
                    TransitionState.ON_SCREEN -> Offset(0f, 0f)
                    TransitionState.STASHED_IN_BACK_STACK -> Offset(-1.0f * width, 0f)
                    TransitionState.DESTROYED -> Offset(1.0f * width, 0f)
                }
            })


        return Modifier.offset(Dp(offset.value.x), Dp(offset.value.y))

    }
}

@Composable
fun rememberBackstackSlider(
    transitionSpec: TransitionSpec<TransitionState, Offset> = { spring(stiffness = Spring.StiffnessVeryLow) },
    clipToBounds: Boolean = false
): UpdateTransitionHandler<TransitionState> = remember {
    BackStackSlider(transitionSpec = transitionSpec, clipToBounds = clipToBounds)
}
