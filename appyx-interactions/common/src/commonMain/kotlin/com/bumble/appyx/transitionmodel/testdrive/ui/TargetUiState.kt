package com.bumble.appyx.transitionmodel.testdrive.ui

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Position


class TargetUiState(
    val position: Position.Target,
    val backgroundColor: BackgroundColor.Target,
) {

    fun toMutableState(uiContext: UiContext): MutableUiState =
        MutableUiState(
            uiContext = uiContext,
            position = Position(uiContext, position),
            backgroundColor = BackgroundColor(uiContext, backgroundColor)
        )
}
