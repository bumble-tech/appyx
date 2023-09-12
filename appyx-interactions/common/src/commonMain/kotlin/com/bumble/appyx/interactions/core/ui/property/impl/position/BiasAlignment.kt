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

    /**
     * Aligns elements outside of the parent bounds.
     * (0,0) will be placed with zero offset relative to the parent bounds.
     * (1,1) will have the offset that equals to parent's width and height.
     * (-1,-1) will have the negative offset that equals to parent's width and height.
     */
    class OutsideAlignment(
        override val horizontalBias: Float,
        override val verticalBias: Float
    ) : BiasAlignment(horizontalBias, verticalBias) {

        constructor(horizontalBias: Int, verticalBias: Int) : this(
            horizontalBias.toFloat(),
            verticalBias.toFloat()
        )

        companion object {

            @Stable
            val InContainer = OutsideAlignment(0f, 0f)

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


    /**
     * Aligns the element fully inside the parent.
     * (-1, -1) is the top left corner of the parent.
     * (1, 1) is the bottom right corner of the parent.
     * (0, 0) is the center of the parent.
     * Intermediate values between -1 and 1 are allowed.
     */
    class InsideAlignment(
        @FloatRange(from = -1.0, to = 1.0)
        override val horizontalBias: Float,
        @FloatRange(from = -1.0, to = 1.0)
        override val verticalBias: Float
    ) : BiasAlignment(horizontalBias, verticalBias) {

        companion object {
            fun fractionAlignment(
                @FloatRange(from = 0.0, to = 1.0)
                horizontalBiasFraction: Float,
                @FloatRange(from = 0.0, to = 1.0)
                verticalBiasFraction: Float
            ): InsideAlignment = InsideAlignment(
                horizontalBias = -1f + horizontalBiasFraction * 2f,
                verticalBias = -1f + verticalBiasFraction * 2f
            )

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
