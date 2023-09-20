package com.bumble.appyx.components.experimental.cards.ui

import androidx.compose.ui.unit.DpOffset
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val scale: Scale.Target = Scale.Target(1f),
    val position: PositionOutside.Target = PositionOutside.Target(DpOffset.Zero),
    val rotationZ: RotationZ.Target = RotationZ.Target(0f),
    val zIndex: ZIndex.Target = ZIndex.Target(0f),
)
