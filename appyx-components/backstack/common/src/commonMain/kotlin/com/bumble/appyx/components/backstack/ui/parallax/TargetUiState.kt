package com.bumble.appyx.components.backstack.ui.parallax

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused", "MemberVisibilityCanBePrivate")
@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target = Position.Target(DpOffset.Zero),
) {

    constructor(
        elementWidth: Float,
        offsetMultiplier: Float,
    ) : this(
        position = Position.Target(
            DpOffset(
                x = (offsetMultiplier.coerceIn(-0.5f, 1.5f) * elementWidth).dp,
                y = 0.dp
            )
        ),
    )
}