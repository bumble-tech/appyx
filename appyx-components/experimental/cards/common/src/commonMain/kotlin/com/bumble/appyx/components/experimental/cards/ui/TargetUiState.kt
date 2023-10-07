package com.bumble.appyx.components.experimental.cards.ui

import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val scale: Scale.Target = Scale.Target(1f),
    val positionAlignment: PositionAlignment.Target = PositionAlignment.Target(),
    val rotationZ: RotationZ.Target = RotationZ.Target(0f),
    val zIndex: ZIndex.Target = ZIndex.Target(0f),
)
