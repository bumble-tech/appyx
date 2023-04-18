package com.bumble.appyx.components.backstack.ui.fader

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha

class TargetUiState(
    val alpha: Alpha.Target,
) {

    fun toMutableState(
        uiContext: UiContext,
    ): MutableUiState =
        MutableUiState(
            uiContext = uiContext,
            alpha = Alpha(uiContext, alpha),
        )
}