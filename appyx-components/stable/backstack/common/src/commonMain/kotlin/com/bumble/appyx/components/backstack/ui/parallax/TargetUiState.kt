package com.bumble.appyx.components.backstack.ui.parallax

import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.ColorOverlay
import com.bumble.appyx.interactions.core.ui.property.impl.Shadow
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.OutsideAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused", "MemberVisibilityCanBePrivate", "MagicNumber")
@MutableUiStateSpecs
class TargetUiState(
    val position: PositionOutside.Target = PositionOutside.Target(OutsideAlignment(0f, 0f)),
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
        position = PositionOutside.Target(
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
