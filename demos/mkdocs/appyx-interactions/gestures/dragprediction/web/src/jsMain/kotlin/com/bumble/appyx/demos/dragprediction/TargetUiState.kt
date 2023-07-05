package com.bumble.appyx.demos.dragprediction

import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target,
    val scale: Scale.Target,
    val rotationZ: RotationZ.Target = RotationZ.Target(0f),
    val roundedCorners: RoundedCorners.Target = RoundedCorners.Target(10),
    val backgroundColor: BackgroundColor.Target,
)
