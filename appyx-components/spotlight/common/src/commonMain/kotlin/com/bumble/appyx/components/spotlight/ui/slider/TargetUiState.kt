package com.bumble.appyx.components.spotlight.ui.slider

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs
import com.bumble.appyx.mapState
import kotlinx.coroutines.flow.StateFlow

@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target,
    val scale: Scale.Target,
    val alpha: Alpha.Target,
) {
    /**
     * Take item's own position in the list of elements into account
     */
    constructor(
        base: TargetUiState,
        positionInList: Int,
        elementWidth: Dp
    ) : this(
        position = Position.Target(
            base.position.value.copy(
                x = (positionInList * elementWidth.value).dp
            )
        ),
        scale = base.scale,
        alpha = base.alpha,
    )

    /**
     * Take the dynamically changing scroll into account
     */
    fun toMutableState(
        uiContext: UiContext,
        scrollX: StateFlow<Float>,
        elementWidth: Dp
    ): MutableUiState =
        MutableUiState(
            uiContext = uiContext,
            position = Position(
                uiContext = uiContext,
                target = position,
                displacement = scrollX
                    .mapState(uiContext.coroutineScope) { value ->
                        DpOffset((value * elementWidth.value).dp, 0.dp)
                    },
            ),
            scale = Scale(uiContext, scale),
            alpha = Alpha(uiContext, alpha),
        )
}
