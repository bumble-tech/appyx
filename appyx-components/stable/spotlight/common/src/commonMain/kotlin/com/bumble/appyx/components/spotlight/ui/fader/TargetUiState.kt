package com.bumble.appyx.components.spotlight.ui.fader

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val alpha: Alpha.Target,
) {
    constructor(base: TargetUiState) : this(
        alpha = base.alpha,
    )

    fun toMutableState(
        uiContext: UiContext,
    ): MutableUiState =
        MutableUiState(
            uiContext = uiContext,
            alpha = Alpha(uiContext.coroutineScope, alpha),
        )
}
