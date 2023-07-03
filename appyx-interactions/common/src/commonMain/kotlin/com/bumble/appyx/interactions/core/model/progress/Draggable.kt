package com.bumble.appyx.interactions.core.model.progress

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density

interface Draggable : HasDefaultAnimationSpec<Float> {

    fun onStartDrag(position: Offset)

    fun onDrag(dragAmount: Offset, density: Density)

    fun onDragEnd()
}
