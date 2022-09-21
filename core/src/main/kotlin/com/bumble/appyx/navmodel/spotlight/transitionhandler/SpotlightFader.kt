package com.bumble.appyx.navmodel.spotlight.transitionhandler

import android.annotation.SuppressLint
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.spotlight.Spotlight

@Suppress("TransitionPropertiesLabel")

class SpotlightFader<T>(
    private val transitionSpec: TransitionSpec<Spotlight.State, Float> = { spring() }
) : ModifierTransitionHandler<T, Spotlight.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Spotlight.State>,
        descriptor: TransitionDescriptor<T, Spotlight.State>
    ): Modifier = modifier.composed {
        val alpha = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Spotlight.State.ACTIVE -> 1f
                    else -> 0f
                }
            })

        alpha(alpha.value)
    }
}

@Composable
fun <T> rememberSpotlightFader(
    transitionSpec: TransitionSpec<Spotlight.State, Float> = { spring() }
): ModifierTransitionHandler<T, Spotlight.State> = remember {
    SpotlightFader(transitionSpec = transitionSpec)
}
