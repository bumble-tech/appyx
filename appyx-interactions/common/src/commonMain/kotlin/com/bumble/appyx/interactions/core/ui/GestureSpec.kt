package com.bumble.appyx.interactions.core.ui

import DefaultAnimationSpec
import androidx.compose.animation.core.AnimationSpec

data class GestureSpec(
    val completionThreshold: Float = 0.5f,
    val completeGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    val revertGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec
)
