package com.bumble.appyx.components.spotlight.ui.sliderrotation

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.cutOffCenter
import com.bumble.appyx.interactions.core.ui.math.cutOffCenterSigned
import com.bumble.appyx.interactions.core.ui.math.scaleUpTo
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs
import com.bumble.appyx.mapState
import kotlinx.coroutines.flow.StateFlow

@Suppress("MagicNumber")
@MutableUiStateSpecs
class TargetUiState(
    private val positionInList: Int = 0,
    val position: PositionAlignment.Target,
    val scale: Scale.Target,
    val rotationY: RotationY.Target,
    val alpha: Alpha.Target,
) {
    /**
     * Take item's own position in the list of elements into account
     */
    constructor(
        base: TargetUiState,
        positionInList: Int
    ) : this(
        positionInList = positionInList,
        position = PositionAlignment.Target(
            base.position.value.copy(
                outsideAlignment = BiasAlignment.OutsideAlignment(
                    horizontalBias = positionInList.toFloat(),
                    verticalBias = 0f
                )
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
        scrollX: StateFlow<Float>
    ): MutableUiState {
        return MutableUiState(
            uiContext = uiContext,
            position = PositionAlignment(
                coroutineScope = uiContext.coroutineScope,
                target = position,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    PositionAlignment.Value(
                        outsideAlignment = BiasAlignment.OutsideAlignment(
                            horizontalBias = it,
                            verticalBias = 0f
                        )
                    )
                },
            ),
            scale = Scale(uiContext.coroutineScope, scale,
                displacement = scrollX.mapState(uiContext.coroutineScope) { scroll ->
                    scaleUpTo(cutOffCenter(positionInList.toFloat(), scroll, 0.15f), -0.1f, 1f)
                }
            ),
            rotationY = RotationY(uiContext.coroutineScope, rotationY,
                displacement = scrollX.mapState(uiContext.coroutineScope) { scroll ->
                    scaleUpTo(cutOffCenterSigned(positionInList.toFloat(), scroll, 0.15f), 15f, 1f)
                }
            ),
            alpha = Alpha(uiContext.coroutineScope, alpha),
        )
    }
}
