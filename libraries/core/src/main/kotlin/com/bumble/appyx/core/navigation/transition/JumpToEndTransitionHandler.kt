package com.bumble.appyx.core.navigation.transition

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.animation.core.Transition
import androidx.compose.ui.Modifier

@Suppress("TransitionPropertiesLabel")
class JumpToEndTransitionHandler<T : Parcelable, S : Parcelable> : ModifierTransitionHandler<T, S>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<S>,
        descriptor: TransitionDescriptor<T, S>
    ): Modifier = modifier
}
