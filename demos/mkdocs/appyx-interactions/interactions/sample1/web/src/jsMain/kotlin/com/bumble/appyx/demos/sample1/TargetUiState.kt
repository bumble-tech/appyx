package com.bumble.appyx.demos.sample1

import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val position: PositionInside.Target,
    val roundedCorners: RoundedCorners.Target = RoundedCorners.Target(10),
    val backgroundColor: BackgroundColor.Target,
)
