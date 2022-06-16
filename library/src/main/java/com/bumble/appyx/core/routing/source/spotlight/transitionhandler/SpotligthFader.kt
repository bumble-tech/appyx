package com.bumble.appyx.core.routing.source.spotlight.transitionhandler

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.core.routing.transition.ModifierTransitionHandler
import com.bumble.appyx.core.routing.transition.TransitionDescriptor
import com.bumble.appyx.core.routing.transition.TransitionSpec

@Suppress("TransitionPropertiesLabel")

class SpotlightFader<T>(
    private val transitionSpec: TransitionSpec<Spotlight.TransitionState, Float> = { spring() }
) : ModifierTransitionHandler<T, Spotlight.TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Spotlight.TransitionState>,
        descriptor: TransitionDescriptor<T, Spotlight.TransitionState>
    ): Modifier = modifier.composed {
        val alpha = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Spotlight.TransitionState.ACTIVE -> 1f
                    else -> 0f
                }
            })

        alpha(alpha.value)
    }
}

@Composable
fun <T> rememberSpotlightFader(
    transitionSpec: TransitionSpec<Spotlight.TransitionState, Float> = { spring() }
): ModifierTransitionHandler<T, Spotlight.TransitionState> = remember {
    SpotlightFader(transitionSpec = transitionSpec)
}
