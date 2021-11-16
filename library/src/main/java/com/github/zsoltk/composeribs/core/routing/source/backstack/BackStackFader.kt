package com.github.zsoltk.composeribs.core.routing.source.backstack

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionSpec
import com.github.zsoltk.composeribs.core.routing.transition.ModifierTransitionHandler

@Suppress("TransitionPropertiesLabel")
class BackStackFader(
    private val transitionSpec: TransitionSpec<BackStack.TransitionState, Float> = { tween(1500) }
) : ModifierTransitionHandler<BackStack.TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.TransitionState>,
        transitionBounds: TransitionBounds
    ): Modifier = modifier.composed {
        val alpha = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    BackStack.TransitionState.ON_SCREEN -> 1f
                    else -> 0f
                }
            })

        alpha(alpha.value)
    }
}

@Composable
fun rememberBackstackFader(
    transitionSpec: TransitionSpec<BackStack.TransitionState, Float> = { tween(1500) }
): ModifierTransitionHandler<BackStack.TransitionState> = remember {
    BackStackFader(transitionSpec = transitionSpec)
}
