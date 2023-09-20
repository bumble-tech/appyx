package com.bumble.appyx.components.experimental.promoter.ui

import com.bumble.appyx.interactions.core.ui.property.impl.AngularPosition
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val position: PositionInside.Target,
    val angularPosition: AngularPosition.Target,
    val scale: Scale.Target,
    val rotationY: RotationY.Target,
    val rotationZ: RotationZ.Target,
)
