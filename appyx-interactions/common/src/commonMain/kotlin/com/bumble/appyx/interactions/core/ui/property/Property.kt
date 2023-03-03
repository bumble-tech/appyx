package com.bumble.appyx.interactions.core.ui.property

import androidx.compose.animation.core.AnimationVector

interface Property<T, V : AnimationVector> :
    HasModifier {

    val value: T
}
