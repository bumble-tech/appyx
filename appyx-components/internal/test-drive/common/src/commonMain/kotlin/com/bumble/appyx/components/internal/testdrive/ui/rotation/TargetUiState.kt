package com.bumble.appyx.components.internal.testdrive.ui.rotation

import com.bumble.appyx.interactions.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val positionAlignment: PositionAlignment.Target,
    val rotationZ: RotationZ.Target,
    val backgroundColor: BackgroundColor.Target,
)
