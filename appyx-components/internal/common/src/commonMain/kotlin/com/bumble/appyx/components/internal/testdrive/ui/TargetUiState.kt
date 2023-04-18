package com.bumble.appyx.components.demos.testdrive.ui

import com.bumble.appyx.components.internal.testdrive.ui.MutableUiState
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
