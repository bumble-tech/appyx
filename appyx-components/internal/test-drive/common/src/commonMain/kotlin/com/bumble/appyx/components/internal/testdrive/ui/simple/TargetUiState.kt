package com.bumble.appyx.components.internal.testdrive.ui.simple

import com.bumble.appyx.interactions.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val positionAlignment: PositionAlignment.Target,
    val backgroundColor: BackgroundColor.Target,
)
