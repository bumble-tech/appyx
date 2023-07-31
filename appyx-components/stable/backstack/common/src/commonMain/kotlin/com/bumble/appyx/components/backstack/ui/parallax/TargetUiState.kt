package com.bumble.appyx.components.backstack.ui.parallax

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.ColorOverlay
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Shadow
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused", "MemberVisibilityCanBePrivate")
@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target = Position.Target(DpOffset.Zero),
    val shadow: Shadow.Target,
    val colorOverlay: ColorOverlay.Target,
    //val alpha: Alpha.Target,
    val zIndex: ZIndex.Target,
) {

    constructor(
        elementWidth: Float,
        offsetMultiplier: Float,
        colorOverlay: ColorOverlay.Target = ColorOverlay.Target(0f),
        shadow: Shadow.Target = Shadow.Target(0f),
        alpha: Alpha.Target = Alpha.Target(1f),
        zIndex: ZIndex.Target = ZIndex.Target(1f),
    ) : this(
        position = Position.Target(
            DpOffset(
                x = (offsetMultiplier * elementWidth).dp,
                y = 0.dp
            ),
        ),
        colorOverlay = colorOverlay,
        shadow = shadow,
        //alpha = alpha,
        zIndex = zIndex,
    )
}
