package com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.main

import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs
import com.bumble.appyx.mapState
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs

@Suppress("MagicNumber")
@MutableUiStateSpecs
class TargetUiState(
    private val positionInList: Int = 0,
    val positionAlignment: PositionAlignment.Target,
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
        positionAlignment = PositionAlignment.Target(
            base.positionAlignment.value.copy(
                outsideAlignment = BiasAlignment.OutsideAlignment(
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
    ): MutableUiState = MutableUiState(
        uiContext = uiContext,
        positionAlignment = PositionAlignment(
            coroutineScope = uiContext.coroutineScope,
            target = positionAlignment,
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
            displacement = scrollX.mapState(uiContext.coroutineScope) {
                (abs(positionInList - it) - 0.15f).coerceIn(0f, 0.25f)
            }
        )
    )
}
