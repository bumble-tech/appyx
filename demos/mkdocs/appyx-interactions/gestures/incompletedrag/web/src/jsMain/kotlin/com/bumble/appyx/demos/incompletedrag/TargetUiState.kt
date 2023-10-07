package com.bumble.appyx.demos.incompletedrag

import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val position: PositionAlignment.Target,
    val rotationZ: RotationZ.Target,
    val roundedCorners: RoundedCorners.Target = RoundedCorners.Target(10),
    val backgroundColor: BackgroundColor.Target,
)
