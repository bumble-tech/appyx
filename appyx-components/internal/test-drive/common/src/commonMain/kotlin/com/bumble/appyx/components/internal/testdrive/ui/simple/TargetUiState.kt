package com.bumble.appyx.components.internal.testdrive.ui.simple

import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val position: PositionInside.Target,
    val backgroundColor: BackgroundColor.Target,
)
