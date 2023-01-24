package com.bumble.appyx.interactions.core.ui

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.core.InteractionModel.Companion.DefaultAnimationSpec

data class GestureSpec(
    val completionThreshold: Float = 0.5f,
    val completeGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    val revertGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec
)
