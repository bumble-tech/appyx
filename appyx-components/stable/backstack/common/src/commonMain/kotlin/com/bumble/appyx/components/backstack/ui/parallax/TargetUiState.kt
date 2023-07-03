package com.bumble.appyx.components.backstack.ui.parallax

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
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
    val zIndex: ZIndex.Target,
) {

    constructor(
        elementWidth: Float,
        offsetMultiplier: Float,
        colorOverlay: ColorOverlay.Target = ColorOverlay.Target(0f),
        shadow: Shadow.Target = Shadow.Target(0f),
        zIndex: ZIndex.Target = ZIndex.Target(0f),
    ) : this(
        position = Position.Target(
            DpOffset(
                x = (offsetMultiplier.coerceIn(-0.5f, 1.5f) * elementWidth).dp,
                y = 0.dp
            ),
        ),
        colorOverlay = colorOverlay,
        shadow = shadow,
        zIndex = zIndex,
    )
}
