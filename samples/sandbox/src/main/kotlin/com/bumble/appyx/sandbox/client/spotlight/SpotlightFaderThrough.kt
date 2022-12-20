package com.bumble.appyx.sandbox.client.spotlight

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.navmodel.spotlight.Spotlight

class SpotlightFaderThrough<T>() : ModifierTransitionHandler<T, Spotlight.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Spotlight.State>,
        descriptor: TransitionDescriptor<T, Spotlight.State>
    ): Modifier = modifier.composed {
        val alpha = transition.animateFloat(
            transitionSpec = {
                when (targetState) {
                    Spotlight.State.ACTIVE -> tween(
                        durationMillis = 210,
                        delayMillis = 10,
                        easing = FastOutSlowInEasing
                    )

                    Spotlight.State.INACTIVE_BEFORE,
                    Spotlight.State.INACTIVE_AFTER -> tween(
                        durationMillis = 90,
                        delayMillis = 0,
                        easing = FastOutSlowInEasing
                    )
                }

            },
            targetValueByState = {
                when (it) {
                    Spotlight.State.ACTIVE -> 1f
                    else -> 0f
                }
            }, label = ""
        )
        val scale = transition.animateFloat(
            transitionSpec = {
                when (targetState) {
                    Spotlight.State.ACTIVE -> tween(
                        durationMillis = 210,
                        delayMillis = 10,
                        easing = FastOutSlowInEasing
                    )

                    Spotlight.State.INACTIVE_BEFORE,
                    Spotlight.State.INACTIVE_AFTER -> tween(
                        durationMillis = 90,
                        delayMillis = 0,
                        easing = FastOutSlowInEasing
                    )
                }

            },
            targetValueByState = {
                when (it) {
                    Spotlight.State.ACTIVE -> 1f
                    else -> 0.92f
                }
            }, label = ""
        )

        if (transition.targetState == Spotlight.State.ACTIVE) {
            scale(scale.value).alpha(alpha.value)
        } else {
            alpha(alpha.value)
        }

    }
}

@Composable
fun <T> rememberSpotlightFaderThrough(): ModifierTransitionHandler<T, Spotlight.State> = remember {
    SpotlightFaderThrough()
}