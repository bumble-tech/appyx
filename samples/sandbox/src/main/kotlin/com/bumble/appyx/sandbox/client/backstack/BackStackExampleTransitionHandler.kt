package com.bumble.appyx.sandbox.client.backstack

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.Replace
import com.bumble.appyx.navmodel.backstack.transitionhandler.BackStackFader
import com.bumble.appyx.navmodel.backstack.transitionhandler.BackStackSlider

class BackStackExampleTransitionHandler<T : Parcelable> :
    ModifierTransitionHandler<T, BackStack.State>(clipToBounds = true) {

    private val slider = BackStackSlider<T>(clipToBounds = clipToBounds)
    private val fader = BackStackFader<T>()

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.State>,
        descriptor: TransitionDescriptor<T, BackStack.State>
    ): Modifier =
        when (descriptor.operation) {
            is Replace -> fader.createModifier(modifier, transition, descriptor)
            else -> slider.createModifier(modifier, transition, descriptor)
        }
}

@Composable
fun <T : Parcelable> rememberBackStackExampleTransitionHandler(
): ModifierTransitionHandler<T, BackStack.State> = remember {
    BackStackExampleTransitionHandler()
}
