package com.bumble.appyx.components.spotlight.ui.slider

import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.ui.property.impl.Alpha
import com.bumble.appyx.interactions.ui.property.impl.Scale
import com.bumble.appyx.interactions.ui.property.impl.position.BiasAlignment.InsideAlignment
import com.bumble.appyx.interactions.ui.property.impl.position.BiasAlignment.OutsideAlignment
import com.bumble.appyx.interactions.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.ui.state.MutableUiStateSpecs
import com.bumble.appyx.mapState
import kotlinx.coroutines.flow.StateFlow

@MutableUiStateSpecs
class TargetUiState(
    private val positionInList: Int = 0,
    val positionAlignment: PositionAlignment.Target,
    val scale: Scale.Target,
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
        positionAlignment = PositionAlignment.Target(
            with(base.positionAlignment.value) {
                copy(
                    outsideAlignment = OutsideAlignment(
                        horizontalBias = outsideAlignment.horizontalBias + positionInList.toFloat(),
                        verticalBias = outsideAlignment.verticalBias
                    )
                )
            }

        ),
        scale = base.scale,
        alpha = base.alpha,
    )

    /**
     * Takes the dynamically changing scroll into account when calculating values.
     *
     * TODO support RTL and Orientation.Vertical
     */
    fun toMutableUiState(
        uiContext: UiContext,
        scrollX: StateFlow<Float>
    ): MutableUiState =
        MutableUiState(
            uiContext = uiContext,
            positionAlignment = PositionAlignment(
                coroutineScope = uiContext.coroutineScope,
                target = positionAlignment,
                displacement = scrollX.mapState(uiContext.coroutineScope) { scrollX ->
                    PositionAlignment.Value(
                        insideAlignment = InsideAlignment(
                          horizontalBias = 0f,
                            verticalBias = 0f,
                        ),
                        outsideAlignment = OutsideAlignment(
                            horizontalBias = scrollX,
                            verticalBias = 0f
                        )
                    )
                },
            ),
            scale = Scale(uiContext.coroutineScope, scale),
            alpha = Alpha(uiContext.coroutineScope, alpha),
        )
}
