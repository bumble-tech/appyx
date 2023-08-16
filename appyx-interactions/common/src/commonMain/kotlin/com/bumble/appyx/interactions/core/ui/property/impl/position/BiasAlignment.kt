package com.bumble.appyx.interactions.core.ui.property.impl.position

import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.bumble.appyx.interactions.core.annotations.FloatRange
import kotlin.math.roundToInt

sealed class BiasAlignment(
    open val horizontalBias: Float,
    open val verticalBias: Float
) : Alignment {

    class OutsideAlignment(
        override val horizontalBias: Float,
        override val verticalBias: Float
    ) : BiasAlignment(horizontalBias, verticalBias) {

        constructor(horizontalBias: Int, verticalBias: Int) : this(horizontalBias.toFloat(), verticalBias.toFloat())

        companion object {

            @Stable
            val Center = OutsideAlignment(0f, 0f)

            @Stable
            val OutsideLeft = OutsideAlignment(-1f, 0f)

            @Stable
            val OutsideTopLeft = OutsideAlignment(-1f, -1f)

            @Stable
            val OutsideBottomLeft = OutsideAlignment(-1f, 1f)

            @Stable
            val OutsideRight = OutsideAlignment(1f, 0f)

            @Stable
            val OutsideBottomRight = OutsideAlignment(1f, 1f)

            @Stable
            val OutsideTopRight = OutsideAlignment(1f, -1f)

            @Stable
            val OutsideBottom = OutsideAlignment(0f, 1f)

            @Stable
            val OutsideTop = OutsideAlignment(0f, -1f)
        }

        override fun align(
            size: IntSize,
            space: IntSize,
            layoutDirection: LayoutDirection
        ): IntOffset {
            return IntOffset(
                (horizontalBias * space.width).roundToInt(),
                (verticalBias * space.height).roundToInt()
            )
        }
    }


    class InsideAlignment(
        @FloatRange(from = -1.0, to = 1.0)
        override val horizontalBias: Float,
        @FloatRange(from = -1.0, to = 1.0)
        override val verticalBias: Float
    ) : BiasAlignment(horizontalBias, verticalBias) {

        companion object {
            @Stable
            val TopStart = InsideAlignment(-1f, -1f)

            @Stable
            val TopCenter = InsideAlignment(0f, -1f)

            @Stable
            val TopEnd = InsideAlignment(1f, -1f)

            @Stable
            val CenterStart = InsideAlignment(-1f, 0f)

            @Stable
            val Center = InsideAlignment(0f, 0f)

            @Stable
            val CenterEnd = InsideAlignment(1f, 0f)

            @Stable
            val BottomStart = InsideAlignment(-1f, 1f)

            @Stable
            val BottomCenter = InsideAlignment(0f, 1f)

            @Stable
            val BottomEnd = InsideAlignment(1f, 1f)
        }

        override fun align(
            size: IntSize,
            space: IntSize,
            layoutDirection: LayoutDirection
        ): IntOffset {
            // Convert to Px first and only round at the end, to avoid rounding twice while calculating
            // the new positions
            val centerX = (space.width - size.width).toFloat() / 2f
            val centerY = (space.height - size.height).toFloat() / 2f
            val resolvedHorizontalBias = if (layoutDirection == LayoutDirection.Ltr) {
                horizontalBias
            } else {
                -1 * horizontalBias
            }

            val x = centerX * (1 + resolvedHorizontalBias)
            val y = centerY * (1 + verticalBias)
            return IntOffset(x.roundToInt(), y.roundToInt())
        }
    }
}
