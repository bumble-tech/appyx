package com.bumble.appyx.navmodel.backstack.transitionhandler

import android.annotation.SuppressLint
import android.os.Parcelable
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
import com.bumble.appyx.navmodel.backstack.BackStack

@Suppress("TransitionPropertiesLabel")
class BackStackFader<T : Parcelable>(
    private val transitionSpec: TransitionSpec<BackStack.State, Float> = { spring() }
) : ModifierTransitionHandler<T, BackStack.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.State>,
        descriptor: TransitionDescriptor<T, BackStack.State>
    ): Modifier = modifier.composed {
        val alpha = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    BackStack.State.ACTIVE -> 1f
                    else -> 0f
                }
            })

        alpha(alpha.value)
    }
}

@Composable
fun <T : Parcelable> rememberBackstackFader(
    transitionSpec: TransitionSpec<BackStack.State, Float> = { spring() }
): ModifierTransitionHandler<T, BackStack.State> = remember {
    BackStackFader(transitionSpec = transitionSpec)
}
