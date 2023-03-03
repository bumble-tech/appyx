package com.bumble.appyx.navigation.node.teaser.backstack.transitionhandler

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
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
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.navmodel.backstack.BackStack
import kotlin.math.roundToInt

@Suppress("TransitionPropertiesLabel")
class CustomBackStackTransitionHandler<T> : ModifierTransitionHandler<T, BackStack.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.State>,
        descriptor: TransitionDescriptor<T, BackStack.State>
    ): Modifier = modifier.composed {
        val alpha = transition.animateFloat(
            transitionSpec = { spring(stiffness = Spring.StiffnessVeryLow) },
            targetValueByState = {
                when (it) {
                    BackStack.State.STASHED,
                    BackStack.State.DESTROYED -> 0f
                    else -> 1f
                }
            })

        val scale = transition.animateFloat(
            transitionSpec = { spring(stiffness = Spring.StiffnessVeryLow) },
            targetValueByState = {
                when (it) {
                    BackStack.State.DESTROYED -> 10f
                    else -> 1f
                }
            })

        val offset = transition.animateOffset(
            transitionSpec = { spring(stiffness = Spring.StiffnessVeryLow) },
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    BackStack.State.CREATED -> toOutsideRight(width)
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

    private fun toCenter() = Offset(0f, 0f)
}

@Composable
fun <T> rememberCustomHandler(): ModifierTransitionHandler<T, BackStack.State> = remember {
    CustomBackStackTransitionHandler()
}
