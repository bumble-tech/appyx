package com.bumble.appyx.components.backstack.ui.slider

import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment.Companion.InContainer
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment.Companion.OutsideRight
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val positionAlignment: PositionAlignment.Target,
    val alpha: Alpha.Target,
) {
    fun toOutsideLeft(multiplier: Float) = TargetUiState(
        positionAlignment = PositionAlignment.Target(
            outsideAlignment = BiasAlignment.OutsideAlignment(
                horizontalBias = multiplier,
                verticalBias = 0f
            )
        ),
        alpha = alpha
    )

    fun toOutsideRight() = TargetUiState(
        positionAlignment = PositionAlignment.Target(
            outsideAlignment = OutsideRight
        ),
        alpha = alpha
    )

    fun toNoOffset() = TargetUiState(
        positionAlignment = PositionAlignment.Target(
            outsideAlignment = InContainer
        ),
        alpha = alpha
    )
}
