package com.bumble.appyx.components.backstack.ui.parallax

import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.ColorOverlay
import com.bumble.appyx.interactions.core.ui.property.impl.Shadow
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment.Companion.InContainer
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused", "MemberVisibilityCanBePrivate", "MagicNumber")
@MutableUiStateSpecs
class TargetUiState(
    val positionAlignment: PositionAlignment.Target = PositionAlignment.Target(InContainer),
    val shadow: Shadow.Target,
    val colorOverlay: ColorOverlay.Target,
    val alpha: Alpha.Target,
) {

    constructor(
        offsetMultiplier: Float,
        colorOverlay: ColorOverlay.Target = ColorOverlay.Target(0f),
        shadow: Shadow.Target = Shadow.Target(0f),
        alpha: Alpha.Target = Alpha.Target(1f),
    ) : this(
        positionAlignment = PositionAlignment.Target(
            OutsideAlignment(
                horizontalBias = offsetMultiplier.coerceIn(-0.5f, 1.5f),
                verticalBias = 0f
            ),
        ),
        colorOverlay = colorOverlay,
        shadow = shadow,
        alpha = alpha,
    )
}
