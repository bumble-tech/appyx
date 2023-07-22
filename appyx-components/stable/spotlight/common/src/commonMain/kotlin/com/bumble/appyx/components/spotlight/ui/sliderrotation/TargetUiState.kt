package com.bumble.appyx.components.spotlight.ui.sliderrotation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.cutOffCenter
import com.bumble.appyx.interactions.core.ui.math.cutOffCenterSigned
import com.bumble.appyx.interactions.core.ui.math.scaleUpTo
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs
import com.bumble.appyx.mapState
import kotlinx.coroutines.flow.StateFlow

@MutableUiStateSpecs
class TargetUiState(
    private val positionInList: Int = 0,
    val position: Position.Target,
    val scale: Scale.Target,
    val rotationY: RotationY.Target,
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
            base.position.value.offset.copy(
                x = (positionInList * elementWidth.value).dp
            )
        ),
        scale = base.scale,
        rotationY = base.rotationY,
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
    ): MutableUiState {
        return MutableUiState(
            uiContext = uiContext,
            position = Position(
                uiContext = uiContext,
                target = position,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    Position.Value(offset = DpOffset((it * elementWidth.value).dp, 0.dp))
                },
            ),
            scale = Scale(uiContext, scale,
                displacement = scrollX.mapState(uiContext.coroutineScope) { scroll ->
                    scaleUpTo(cutOffCenter(positionInList.toFloat(), scroll, 0.15f), -0.1f, 1f)
                }
            ),
            rotationY = RotationY(uiContext, rotationY,
                displacement = scrollX.mapState(uiContext.coroutineScope) { scroll ->
                    scaleUpTo(cutOffCenterSigned(positionInList.toFloat(), scroll, 0.15f), 15f, 1f)
                }
            ),
            alpha = Alpha(uiContext, alpha),
        )
    }
}
