package com.bumble.appyx.app.node.teaser.backstack.transitionhandler

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.bumble.appyx.core.routing.transition.ModifierTransitionHandler
import com.bumble.appyx.core.routing.transition.TransitionDescriptor
import com.bumble.appyx.routingsource.backstack.BackStack
import kotlin.math.roundToInt

@Suppress("TransitionPropertiesLabel")
class CustomBackStackTransitionHandler<T> : ModifierTransitionHandler<T, BackStack.TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.TransitionState>,
        descriptor: TransitionDescriptor<T, BackStack.TransitionState>
    ): Modifier = modifier.composed {
        val alpha = transition.animateFloat(
            transitionSpec = { spring() },
            targetValueByState = {
                when (it) {
                    BackStack.TransitionState.STASHED_IN_BACK_STACK,
                    BackStack.TransitionState.DESTROYED -> 0f
                    else -> 1f
                }
            })

        val scale = transition.animateFloat(
            transitionSpec = { spring() },
            targetValueByState = {
                when (it) {
                    BackStack.TransitionState.DESTROYED -> 10f
                    else -> 1f
                }
            })

        val offset = transition.animateOffset(
            transitionSpec = { spring() },
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    BackStack.TransitionState.CREATED -> toOutsideRight(width)
                    else -> toCenter()
                }
            })

        return@composed this
            .alpha(alpha.value)
            .scale(scale.value)
            .offset {
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
fun <T> rememberCustomHandler(): ModifierTransitionHandler<T, BackStack.TransitionState> = remember {
    CustomBackStackTransitionHandler()
}
