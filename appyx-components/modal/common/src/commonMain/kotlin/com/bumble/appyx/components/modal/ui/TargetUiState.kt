package com.bumble.appyx.components.modal.ui

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.*
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val width: Width.Target,
    val height: Height.Target,
    val position: Position.Target,
    val corner: BackgroundCorner.Target,
) {

    fun toMutableState(
        uiContext: UiContext
    ): MutableUiState =
        MutableUiState(
            uiContext = uiContext,
            width = Width(uiContext, width),
            height = Height(uiContext, height),
            position = Position(uiContext, position),
            corner = BackgroundCorner(uiContext, corner),
        )
}
