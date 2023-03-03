package com.bumble.appyx.interactions.core.model.progress

import androidx.compose.animation.core.AnimationSpec

interface HasDefaultAnimationSpec<T> {

    val defaultAnimationSpec: AnimationSpec<T>
}
