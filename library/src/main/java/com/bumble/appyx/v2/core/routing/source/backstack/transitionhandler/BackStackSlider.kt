package com.bumble.appyx.v2.core.routing.source.backstack.transitionhandler

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.operation.BackStackOperation
import com.bumble.appyx.v2.core.routing.source.backstack.operation.NewRoot
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Pop
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Push
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Remove
import com.bumble.appyx.v2.core.routing.source.backstack.operation.Replace
import com.bumble.appyx.v2.core.routing.source.backstack.operation.SingleTop.SingleTopReactivateBackStackOperation
import com.bumble.appyx.v2.core.routing.source.backstack.operation.SingleTop.SingleTopReplaceBackStackOperation
import com.bumble.appyx.v2.core.routing.transition.ModifierTransitionHandler
import com.bumble.appyx.v2.core.routing.transition.TransitionDescriptor
import com.bumble.appyx.v2.core.routing.transition.TransitionSpec
import kotlin.math.roundToInt

@Suppress("TransitionPropertiesLabel")
class BackStackSlider<T>(
    private val transitionSpec: TransitionSpec<BackStack.TransitionState, Offset> = {
        spring(stiffness = Spring.StiffnessVeryLow)
    },
    override val clipToBounds: Boolean = false
) : ModifierTransitionHandler<T, BackStack.TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.TransitionState>,
        descriptor: TransitionDescriptor<T, BackStack.TransitionState>
    ): Modifier = modifier.composed {
        val offset = transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    BackStack.TransitionState.CREATED -> toOutsideRight(width)
                    BackStack.TransitionState.ACTIVE -> toCenter()
                    BackStack.TransitionState.STASHED_IN_BACK_STACK -> toOutsideLeft(width)
                    BackStack.TransitionState.DESTROYED -> {
                        when (val operation = descriptor.operation as? BackStackOperation) {
                            is Push,
                            is Pop,
                            is Remove,
                            is SingleTopReactivateBackStackOperation -> toOutsideRight(width)
                            is Replace,
                            is NewRoot,
                            is SingleTopReplaceBackStackOperation -> toOutsideLeft(width)
                            null -> error("Unexpected operation: $operation")
                        }
                    }
                }
            })

        offset {
            IntOffset(
                x = (offset.value.x * this.density).roundToInt(),
                y = (offset.value.y * this.density).roundToInt()
            )
        }
    }

    private fun toOutsideRight(width: Float) = Offset(1.0f * width, 0f)

    private fun toOutsideLeft(width: Float) = Offset(-1.0f * width, 0f)

    private fun toCenter() = Offset(0f, 0f)
}

@Composable
fun <T> rememberBackstackSlider(
    transitionSpec: TransitionSpec<BackStack.TransitionState, Offset> = { spring(stiffness = Spring.StiffnessVeryLow) },
    clipToBounds: Boolean = false
): ModifierTransitionHandler<T, BackStack.TransitionState> = remember {
    BackStackSlider(transitionSpec = transitionSpec, clipToBounds = clipToBounds)
}
