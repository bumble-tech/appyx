package com.bumble.appyx.components.spotlight.ui.stack3d

import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.GenericFloatProperty
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val effectiveIndex: GenericFloatProperty.Target,
    val position: Position.Target,
    val scale: Scale.Target,
    val alpha: Alpha.Target,
    val zIndex: ZIndex.Target,
) {
    override fun toString(): String =
        StringBuilder().apply {
            append("[effectiveIndex: ${effectiveIndex.value}, position: ${position.value}, scale: ${scale.value}, alpha: ${alpha.value}, zIndex: ${zIndex.value}]")
        }.toString()
}
