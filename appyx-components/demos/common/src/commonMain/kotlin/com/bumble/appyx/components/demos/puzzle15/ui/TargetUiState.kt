package com.bumble.appyx.components.demos.puzzle15.ui

import androidx.compose.ui.unit.DpOffset
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target = Position.Target(DpOffset.Zero)
)