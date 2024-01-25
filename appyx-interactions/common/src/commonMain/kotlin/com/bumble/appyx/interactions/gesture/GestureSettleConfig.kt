package com.bumble.appyx.interactions.gesture

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Immutable
import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec

@Immutable
data class GestureSettleConfig(
    val completionThreshold: Float = 0.5f,
    val completeGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec,
    val revertGestureSpec: AnimationSpec<Float> = DefaultAnimationSpec,
)
