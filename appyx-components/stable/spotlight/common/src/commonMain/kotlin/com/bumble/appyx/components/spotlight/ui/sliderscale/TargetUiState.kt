package com.bumble.appyx.components.spotlight.ui.sliderscale

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs
import com.bumble.appyx.mapState
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs

@Suppress("MagicNumber")
@MutableUiStateSpecs
class TargetUiState(
    private val positionInList: Int = 0,
    val position: PositionOutside.Target,
    val scale: Scale.Target,
) {
    /**
     * Take item's own position in the list of elements into account
     */
    constructor(
        base: TargetUiState,
        positionInList: Int
    ) : this(
        positionInList = positionInList,
        position = PositionOutside.Target(
            base.position.value.copy(
                BiasAlignment.OutsideAlignment(
                    horizontalBias = positionInList.toFloat(),
                    verticalBias = 0f
                )
            )
        ),
        scale = base.scale,
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
            position = PositionOutside(
                coroutineScope = uiContext.coroutineScope,
                target = position,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    PositionOutside.Value(
                        alignment = BiasAlignment.OutsideAlignment(
                            horizontalBias = it,
                            verticalBias = 0f
                        )
                    )
                },
            ),
            scale = Scale(uiContext.coroutineScope, scale,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    (abs(positionInList - it) - 0.15f).coerceIn(0f, 0.25f)
                }
            )
        )
    }
}
