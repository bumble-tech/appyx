package com.bumble.appyx.interactions.core.ui.gesture

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Immutable

@Immutable
data class GestureSettleConfig(
    val completionThreshold: Float = 0.5f,
    val completeGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    val revertGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec,
)

val DefaultAnimationSpec: SpringSpec<Float> = spring()
