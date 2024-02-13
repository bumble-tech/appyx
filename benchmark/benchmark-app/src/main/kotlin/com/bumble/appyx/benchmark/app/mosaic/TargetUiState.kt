package com.bumble.appyx.benchmark.app.mosaic

import com.bumble.appyx.interactions.ui.property.impl.AngularPosition
import com.bumble.appyx.interactions.ui.property.impl.RotationY
import com.bumble.appyx.interactions.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.ui.property.impl.position.PositionOffset

data class TargetUiState(
    val position: PositionAlignment.Target,
    val positionOffset: PositionOffset.Target = PositionOffset.Target(),
    val angularPosition: AngularPosition.Target = AngularPosition.Target(
        AngularPosition.Value.Zero
    ),
    val rotationZ: RotationZ.Target = RotationZ.Target(0f),
    val rotationY: RotationY.Target = RotationY.Target(0f),
    val roundedCorners: RoundedCorners.Target = RoundedCorners.Target(0)
)
