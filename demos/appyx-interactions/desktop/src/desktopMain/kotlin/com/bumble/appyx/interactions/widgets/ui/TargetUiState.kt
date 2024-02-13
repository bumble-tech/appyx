package com.bumble.appyx.interactions.widgets.ui

import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.ui.context.UiContext
import com.bumble.appyx.interactions.ui.math.clamp
import com.bumble.appyx.interactions.ui.math.smoothstep
import com.bumble.appyx.interactions.ui.property.impl.Alpha
import com.bumble.appyx.interactions.ui.property.impl.RotationX
import com.bumble.appyx.interactions.ui.property.impl.Scale
import com.bumble.appyx.interactions.ui.property.impl.ZIndex
import com.bumble.appyx.interactions.ui.property.impl.position.PositionOffset
import com.bumble.appyx.interactions.ui.state.MutableUiStateSpecs
import com.bumble.appyx.mapState
import kotlinx.coroutines.flow.StateFlow

@MutableUiStateSpecs
class TargetUiState(
    val positionInList: Int = 0,
    val rotationX: RotationX.Target,
    val positionOffset: PositionOffset.Target,
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
        positionOffset = base.positionOffset,
        scale = base.scale,
        alpha = base.alpha,
        zIndex = base.zIndex,
    )

    fun toMutableUiState(
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
            positionOffset = PositionOffset(
                coroutineScope = uiContext.coroutineScope,
                target = positionOffset,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    val factor = 0.075f + smoothstep(0f, 1f, it)
                    PositionOffset.Value(
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
