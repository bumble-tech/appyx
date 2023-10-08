package com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.backdrop

import androidx.compose.ui.unit.DpOffset
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.cutOffCenterSigned
import com.bumble.appyx.interactions.core.ui.math.scaleUpTo
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Height
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.property.impl.Width
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOffset
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs
import com.bumble.appyx.mapState
import kotlinx.coroutines.flow.StateFlow

@Suppress("MagicNumber")
@MutableUiStateSpecs
class TargetUiState(
    private val positionInList: Int = 0,
    val positionAlignment: PositionAlignment.Target = PositionAlignment.Target(),
    val positionOffset: PositionOffset.Target = PositionOffset.Target(DpOffset.Zero),
    val width: Width.Target = Width.Target(1f),
    val height: Height.Target = Height.Target(1f),
    val rotationY: RotationY.Target = RotationY.Target(0f),
    val alpha: Alpha.Target = Alpha.Target(1f),
    val roundedCorners: RoundedCorners.Target = RoundedCorners.Target(0),
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
                    outsideAlignment = BiasAlignment.OutsideAlignment(
                        horizontalBias = outsideAlignment.horizontalBias + positionInList.toFloat(),
                        verticalBias = outsideAlignment.verticalBias
                    )
                )
            }
        ),
        positionOffset = base.positionOffset,
        width = base.width,
        height = base.height,
        roundedCorners = base.roundedCorners,
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
            positionAlignment = PositionAlignment(
                coroutineScope = uiContext.coroutineScope,
                target = positionAlignment,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    PositionAlignment.Value(
                        outsideAlignment = BiasAlignment.OutsideAlignment(
                            horizontalBias = it,
                            verticalBias = 0f
                        ),
                        insideAlignment = BiasAlignment.InsideAlignment(
                            horizontalBias = 0f,
                            verticalBias = 0f
                        )
                    )
                },
            ),
            positionOffset = PositionOffset(
                coroutineScope = uiContext.coroutineScope,
                target = positionOffset,
            ),
            width = Width(uiContext.coroutineScope, width),
            height = Height(uiContext.coroutineScope, height),
            roundedCorners = RoundedCorners(uiContext.coroutineScope, roundedCorners),
            rotationY = RotationY(uiContext.coroutineScope, rotationY,
                displacement = scrollX.mapState(uiContext.coroutineScope) { scroll ->
                    scaleUpTo(cutOffCenterSigned(positionInList.toFloat(), scroll, 0.15f), 15f, 1f)
                }
            ),
            alpha = Alpha(uiContext.coroutineScope, alpha),
        )
    }
}
