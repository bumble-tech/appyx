package com.bumble.appyx.components.spotlight.ui.fader

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha

class TargetUiState(
    val alpha: Alpha.Target,
) {
    /**
     * Take item's own position in the list of elements into account
     */
    constructor(
        base: TargetUiState
    ) : this(alpha = base.alpha)

    fun toMutableState(
        uiContext: UiContext
    ): MutableUiState {
        return MutableUiState(
            uiContext = uiContext,
            alpha = Alpha(uiContext, alpha)
        )
    }
}
