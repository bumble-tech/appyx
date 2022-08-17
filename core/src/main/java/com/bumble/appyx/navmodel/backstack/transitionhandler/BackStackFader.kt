package com.bumble.appyx.navmodel.backstack.transitionhandler

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec

@Suppress("TransitionPropertiesLabel")
class BackStackFader<T>(
    private val transitionSpec: TransitionSpec<BackStack.TransitionState, Float> = { spring() }
) : ModifierTransitionHandler<T, BackStack.TransitionState>() {

    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.TransitionState>,
        descriptor: TransitionDescriptor<T, BackStack.TransitionState>
    ): Modifier = modifier.composed {
        val alpha = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    BackStack.TransitionState.ACTIVE -> 1f
                    else -> 0f
                }
            })

        alpha(alpha.value)
    }
}

@Composable
fun <T> rememberBackstackFader(
    transitionSpec: TransitionSpec<BackStack.TransitionState, Float> = { spring() }
): ModifierTransitionHandler<T, BackStack.TransitionState> = remember {
    BackStackFader(transitionSpec = transitionSpec)
}
