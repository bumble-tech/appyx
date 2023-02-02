package com.bumble.appyx.interactions.core.ui.property

import androidx.compose.animation.core.AnimationSpec

// TODO better name?
interface AnimatableHolder<T> {

    suspend fun snapTo(targetValue: T)

    suspend fun animateTo(targetValue: T, animationSpec: AnimationSpec<T>)
}
