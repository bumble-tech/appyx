package com.bumble.appyx.components.experimental.puzzle15.ui

import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val positionAlignment: PositionAlignment.Target = PositionAlignment.Target()
)
