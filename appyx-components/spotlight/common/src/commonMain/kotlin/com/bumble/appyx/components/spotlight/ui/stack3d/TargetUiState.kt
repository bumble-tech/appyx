package com.bumble.appyx.components.spotlight.ui.stack3d

import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.interactions.core.ui.property.impl.Position
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
    val position: Position.Target,
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
    ): MutableUiState {
        return MutableUiState(
            uiContext = uiContext,
            rotationX = RotationX(
                uiContext = uiContext,
                target = rotationX,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    2.5f * clamp(-it, 0f, 1f)
                },
                origin = TransformOrigin(0.075f, 0f),
            ),
            position = Position(
                uiContext = uiContext,
                target = position,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    val factor = 0.075f + smoothstep(0f, 1f, it)
                    DpOffset(
                        x = (-0.125f * it * itemWidth.value).dp,
                        y = (-factor * it * itemHeight.value / (1f - 0.1f * it)).dp,
                    )
                }
            ),
            scale = Scale(
                uiContext = uiContext,
                target = scale,
                displacement = scrollX.mapState(uiContext.coroutineScope) { -0.1f * it },
            ),
            alpha = Alpha(
                uiContext = uiContext,
                target = alpha,
                displacement = scrollX.mapState(uiContext.coroutineScope) {
                    clamp(it, 0f, 1f) + clamp(-it - 3f, 0f, 1f)
                },
            ),
            zIndex = ZIndex(
                uiContext = uiContext,
                target = zIndex,
                displacement = scrollX.mapState(uiContext.coroutineScope) { -it }
            ),
        )
    }

    private companion object {

        /**
         * Based on the OpenGL Shading Language clamp function.
         * https://registry.khronos.org/OpenGL-Refpages/gl4/html/clamp.xhtml
         */
        fun clamp(x: Float, min: Float, max: Float): Float =
            x.coerceIn(min, max)

        /**
         * Based on the OpenGL Shading Language smoothstep function.
         * https://registry.khronos.org/OpenGL-Refpages/gl4/html/smoothstep.xhtml
         */
        fun smoothstep(edge0: Float, edge1: Float, x: Float): Float =
            clamp((x - edge0) / (edge1 - edge0), 0f, 1f).run {
                this * this * (3f - 2f * this)
            }
    }
}
