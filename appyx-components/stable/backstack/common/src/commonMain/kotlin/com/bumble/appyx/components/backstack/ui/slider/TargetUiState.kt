package com.bumble.appyx.components.backstack.ui.slider

import androidx.compose.ui.unit.DpOffset
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val position: PositionOutside.Target,
    val alpha: Alpha.Target,
) {
    fun toOutsideLeft() = TargetUiState(
        position = PositionOutside.Target(alignment = BiasAlignment.OutsideAlignment.OutsideLeft),
        alpha = alpha
    )

    fun toOutsideRight() = TargetUiState(
        position = PositionOutside.Target(alignment = BiasAlignment.OutsideAlignment.OutsideRight),
        alpha = alpha
    )

    fun toNoOffset() = TargetUiState(
        position = PositionOutside.Target(DpOffset.Zero),
        alpha = alpha
    )
}
