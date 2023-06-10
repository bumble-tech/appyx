package com.bumble.appyx.components.spotlight.ui.stack3d.simple

import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RotationX
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val rotationX: RotationX.Target,
    val position: Position.Target,
    val scale: Scale.Target,
    val alpha: Alpha.Target,
    val zIndex: ZIndex.Target,
)
