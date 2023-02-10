package com.bumble.appyx.interactions.core.ui.property

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector

// TODO better name?
interface AnimatableHolder<T, V : AnimationVector> {

    suspend fun snapTo(targetValue: T)

    suspend fun animateTo(targetValue: T, animationSpec: AnimationSpec<T>, block: (Animatable<T, V>.() -> Unit))
}
