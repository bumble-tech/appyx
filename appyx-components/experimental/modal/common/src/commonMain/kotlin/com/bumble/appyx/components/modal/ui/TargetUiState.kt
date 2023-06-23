package com.bumble.appyx.components.modal.ui

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Height
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val height: Height.Target,
    val position: Position.Target,
    val corner: RoundedCorners.Target,
) {

    fun toMutableState(
        uiContext: UiContext
    ): MutableUiState =
        MutableUiState(
            uiContext = uiContext,
            height = Height(uiContext, height),
            position = Position(uiContext, position),
            corner = RoundedCorners(uiContext, corner),
        )
}
