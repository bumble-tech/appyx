package com.bumble.appyx.components.spotlight.ui.slider

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.mapState
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs

class TargetUiState(
    private val positionInList: Int = 0,
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
        positionInList = positionInList,
        position = Position.Target(
            base.position.value.copy(
                x = (positionInList * elementWidth.value).dp
            )
        ),
        scale = base.scale,
        alpha = base.alpha,
    )

    /**
     * Takes the dynamically changing scroll into account when calculating values.
     *
     * TODO support RTL and Orientation.Vertical
     */
    fun toMutableState(
        uiContext: UiContext,
        scrollX: StateFlow<Float>,
        elementWidth: Dp
    ): MutableUiState =
        MutableUiState(
            uiContext = uiContext,
            position = Position(
                uiContext, position,
                scrollX.mapState(uiContext.coroutineScope) {
                    DpOffset((it * elementWidth.value).dp, 0.dp)
                },
            ),
            scale = Scale(uiContext, scale,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    (abs(positionInList - it) - 0.15f).coerceIn(0f, 0.25f)
                }
            ),
            alpha = Alpha(uiContext, alpha),
        )
}
