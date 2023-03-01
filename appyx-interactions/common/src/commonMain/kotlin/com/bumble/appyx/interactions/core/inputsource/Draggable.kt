package com.bumble.appyx.interactions.core.inputsource

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density

interface Draggable : HasDefaultAnimationSpec<Float>{

    fun onStartDrag(position: Offset)

    fun onDrag(dragAmount: Offset, density: Density)

    fun onDragEnd(
        completionThreshold: Float = 0.5f,
        completeGestureSpec: AnimationSpec<Float> = defaultAnimationSpec,
        revertGestureSpec: AnimationSpec<Float> = defaultAnimationSpec
    )
}
