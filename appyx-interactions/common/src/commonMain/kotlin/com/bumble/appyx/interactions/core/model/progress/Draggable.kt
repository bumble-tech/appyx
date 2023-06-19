package com.bumble.appyx.interactions.core.model.progress

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig

interface Draggable : HasDefaultAnimationSpec<Float> {

    val gestureSettleConfig: GestureSettleConfig

    fun onStartDrag(position: Offset)

    fun onDrag(dragAmount: Offset, density: Density)

    fun onDragEnd(onSettled: (() -> Unit)? = null)
}
