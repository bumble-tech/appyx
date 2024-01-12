package com.bumble.appyx.components.modal.ui

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Height
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@MutableUiStateSpecs
class TargetUiState(
    val height: Height.Target,
    val positionAlignment: PositionAlignment.Target,
    val corner: RoundedCorners.Target,
) {

    fun toMutableUiState(
        uiContext: UiContext
    ): MutableUiState =
        MutableUiState(
            uiContext = uiContext,
            height = Height(uiContext.coroutineScope, height),
            positionAlignment = PositionAlignment(uiContext.coroutineScope, positionAlignment),
            corner = RoundedCorners(uiContext.coroutineScope, corner),
        )
}
