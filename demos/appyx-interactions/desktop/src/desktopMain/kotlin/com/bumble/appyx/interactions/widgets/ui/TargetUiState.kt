package com.bumble.appyx.interactions.widgets.ui

import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.clamp
import com.bumble.appyx.interactions.core.ui.math.smoothstep
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.RotationX
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs
import com.bumble.appyx.mapState
import kotlinx.coroutines.flow.StateFlow

@MutableUiStateSpecs
class TargetUiState(
    val positionInList: Int = 0,
    val rotationX: RotationX.Target,
    val position: PositionAlignment.Target,
    val scale: Scale.Target,
    val alpha: Alpha.Target,
    val zIndex: ZIndex.Target,
) {
    constructor(
        base: TargetUiState,
        positionInList: Int,
    ) : this(
        positionInList = positionInList,
        rotationX = base.rotationX,
        position = base.position,
        scale = base.scale,
        alpha = base.alpha,
        zIndex = base.zIndex,
    )

    fun toMutableState(
        uiContext: UiContext,
        scrollX: StateFlow<Float>,
        itemWidth: Dp,
        itemHeight: Dp,
        itemsInStack: Int = 3,
    ): MutableUiState {
        return MutableUiState(
            uiContext = uiContext,
            rotationX = RotationX(
                coroutineScope = uiContext.coroutineScope,
                target = rotationX,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    2.5f * clamp(-it, 0f, 1f)
                },
                origin = TransformOrigin(0.075f, 0f),
            ),
            position = PositionAlignment(
                coroutineScope = uiContext.coroutineScope,
                target = position,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    val factor = 0.075f + smoothstep(0f, 1f, it)
                    PositionAlignment.Value(
                        offset = DpOffset(
                            x = (-0.125f * it * itemWidth.value).dp,
                            y = (-factor * it * itemHeight.value / (1f - 0.1f * it)).dp,
                        )
                    )
                }
            ),
            scale = Scale(
                coroutineScope = uiContext.coroutineScope,
                target = scale,
                displacement = scrollX.mapState(uiContext.coroutineScope) { -0.1f * it },
            ),
            alpha = Alpha(
                coroutineScope = uiContext.coroutineScope,
                target = alpha,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    clamp(it, 0f, 1f) + clamp(-it - itemsInStack, 0f, 1f)
                },
            ),
            zIndex = ZIndex(
                coroutineScope = uiContext.coroutineScope,
                target = zIndex,
                displacement = scrollX.mapState(uiContext.coroutineScope) { -it }
            ),
        )
    }
}
