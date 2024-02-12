package com.bumble.appyx.demos.sample1

import com.bumble.appyx.interactions.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.ui.property.impl.position.PositionOffset
import com.bumble.appyx.interactions.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val positionAlignment: PositionAlignment.Target,
    val positionOffset: PositionOffset.Target,
    val roundedCorners: RoundedCorners.Target = RoundedCorners.Target(10),
    val backgroundColor: BackgroundColor.Target,
)
