package com.bumble.appyx.components.backstack.ui.slider

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target,
    val alpha: Alpha.Target,
) {
    fun toOutsideLeft(position: Int, baseOffset: Dp) = TargetUiState(
        position = Position.Target(DpOffset(-baseOffset, 0.dp)),
        alpha = alpha
    )

    fun toOutsideRight(position: Int, baseOffset: Dp) = TargetUiState(
        position = Position.Target(DpOffset(baseOffset, 0.dp)),
        alpha = alpha
    )

    fun toNoOffset() = TargetUiState(
        position = Position.Target(DpOffset.Zero),
        alpha = alpha
    )
}
