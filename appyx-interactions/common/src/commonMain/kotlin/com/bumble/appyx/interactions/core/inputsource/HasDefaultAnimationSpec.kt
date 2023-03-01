package com.bumble.appyx.interactions.core.inputsource

import androidx.compose.animation.core.AnimationSpec

interface HasDefaultAnimationSpec<T> {

    val defaultAnimationSpec: AnimationSpec<T>
}
