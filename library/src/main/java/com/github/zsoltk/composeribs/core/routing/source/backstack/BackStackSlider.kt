package com.github.zsoltk.composeribs.core.routing.source.backstack

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.NewRoot
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Pop
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Push
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Remove
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.Replace
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.SingleTop.SingleTopReactivateBackStackOperation
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.SingleTop.SingleTopReplaceBackStackOperation
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.TransitionState
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionSpec
import com.github.zsoltk.composeribs.core.routing.transition.ModifierTransitionHandler

@Suppress("TransitionPropertiesLabel")
class BackStackSlider(
    private val transitionSpec: TransitionSpec<TransitionState, Offset> = { tween(1500) },
    override val clipToBounds: Boolean = false
) : ModifierTransitionHandler<TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<TransitionState>,
        transitionBounds: TransitionBounds
    ): Modifier = modifier.composed {
        val offset = transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    TransitionState.CREATED -> Offset(1.0f * width, 0f)
                    TransitionState.ON_SCREEN -> Offset(0f, 0f)
                    TransitionState.STASHED_IN_BACK_STACK -> Offset(-1.0f * width, 0f)
                    TransitionState.DESTROYED -> Offset(1.0f * width, 0f)
                }
            })

        offset(Dp(offset.value.x), Dp(offset.value.y))
    }

    private fun toOutsideRight(width: Float) = Offset(1.0f * width, 0f)

    private fun toOutsideLeft(width: Float) = Offset(-1.0f * width, 0f)

    private fun toCenter() = Offset(0f, 0f)
}

@Composable
fun rememberBackstackSlider(
    transitionSpec: TransitionSpec<TransitionState, Offset> = { spring(stiffness = Spring.StiffnessVeryLow) },
    clipToBounds: Boolean = false
): ModifierTransitionHandler<TransitionState> = remember {
    BackStackSlider(transitionSpec = transitionSpec, clipToBounds = clipToBounds)
}
