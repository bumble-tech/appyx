package com.github.zsoltk.composeribs.core.routing.source.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.github.zsoltk.composeribs.core.routing.transition.TransitionSpec
import com.github.zsoltk.composeribs.core.routing.transition.UpdateTransitionHandler

@Suppress("TransitionPropertiesLabel")
class BackStackSlider<T>(
    private val transitionSpec: TransitionSpec<BackStack.TransitionState, Offset> = { tween(1500) },
    override val clipToBounds: Boolean = false
) : UpdateTransitionHandler<T, BackStack.TransitionState>() {

    @Composable
    override fun map(
        transition: Transition<BackStack.TransitionState>,
        descriptor: TransitionDescriptor<T, BackStack.TransitionState>
    ): Modifier {
        val offset = transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    BackStack.TransitionState.CREATED -> toOutsideRight(width)
                    BackStack.TransitionState.ON_SCREEN -> toCenter()
                    BackStack.TransitionState.STASHED_IN_BACK_STACK -> toOutsideLeft(width)
                    BackStack.TransitionState.DESTROYED -> {
                        when (val operation = descriptor.operation) {
                            is Push,
                            is Pop,
                            is Remove,
                            is SingleTopReactivateBackStackOperation -> toOutsideRight(width)
                            is Replace,
                            is NewRoot,
                            is SingleTopReplaceBackStackOperation -> toOutsideLeft(width)
                            else -> throw IllegalArgumentException("Unexpected operation: $operation")
                        }
                    }
                }
            })

        return Modifier.offset(Dp(offset.value.x), Dp(offset.value.y))
    }

    private fun toOutsideRight(width: Float) = Offset(1.0f * width, 0f)

    private fun toOutsideLeft(width: Float) = Offset(-1.0f * width, 0f)

    private fun toCenter() = Offset(0f, 0f)
}
