package com.bumble.appyx.components.spotlight.ui.fader

import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.ui.property.impl.Alpha
import com.bumble.appyx.interactions.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val alpha: Alpha.Target,
) {
    constructor(base: TargetUiState) : this(
        alpha = base.alpha,
    )

    fun toMutableUiState(
        uiContext: UiContext,
    ): MutableUiState =
        MutableUiState(
            uiContext = uiContext,
            alpha = Alpha(uiContext.coroutineScope, alpha),
        )
}
