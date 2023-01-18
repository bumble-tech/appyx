package com.bumble.appyx.interactions.core.inputsource

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density

interface Draggable {

    fun onStartDrag(position: Offset)

    fun onDrag(dragAmount: Offset, density: Density)

    fun onDragEnd()
}
