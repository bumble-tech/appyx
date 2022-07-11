package com.bumble.appyx.routingsource.spotlight.transitionhandler

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import com.bumble.appyx.core.routing.transition.ModifierTransitionHandler
import com.bumble.appyx.core.routing.transition.TransitionDescriptor
import com.bumble.appyx.core.routing.transition.TransitionSpec
import com.bumble.appyx.routingsource.spotlight.Spotlight

@Suppress("TransitionPropertiesLabel")
class SpotlightSlider<T>(
    private val transitionSpec: TransitionSpec<Spotlight.TransitionState, Offset> = {
        spring()
    },
    override val clipToBounds: Boolean = false
) : ModifierTransitionHandler<T, Spotlight.TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Spotlight.TransitionState>,
        descriptor: TransitionDescriptor<T, Spotlight.TransitionState>
    ): Modifier = modifier.composed {
        val offset = transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    Spotlight.TransitionState.INACTIVE_BEFORE -> toOutsideLeft(width)
                    Spotlight.TransitionState.ACTIVE -> toCenter()
                    Spotlight.TransitionState.INACTIVE_AFTER -> toOutsideRight(width)
                }
            },
        )

        offset(Dp(offset.value.x), Dp(offset.value.y))
    }

    private fun toOutsideRight(width: Float) = Offset(1.0f * width, 0f)

    private fun toOutsideLeft(width: Float) = Offset(-1.0f * width, 0f)

    private fun toCenter() = Offset(0f, 0f)
}

@Composable
fun <T> rememberSpotlightSlider(
    transitionSpec: TransitionSpec<Spotlight.TransitionState, Offset> = { spring() },
    clipToBounds: Boolean = false
): ModifierTransitionHandler<T, Spotlight.TransitionState> = remember {
    SpotlightSlider(transitionSpec = transitionSpec, clipToBounds = clipToBounds)
}
