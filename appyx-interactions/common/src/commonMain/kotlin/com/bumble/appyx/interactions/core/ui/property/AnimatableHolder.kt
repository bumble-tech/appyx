package com.bumble.appyx.interactions.core.ui.property

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import kotlinx.coroutines.flow.Flow

// TODO better name?
interface AnimatableHolder<T, V : AnimationVector> {

    val isAnimatingFlow: Flow<Boolean>

    suspend fun snapTo(targetValue: T)

    suspend fun animateTo(
        targetValue: T,
        animationSpec: AnimationSpec<T>,
        block: (Animatable<T, V>.() -> Unit)
    )
}
