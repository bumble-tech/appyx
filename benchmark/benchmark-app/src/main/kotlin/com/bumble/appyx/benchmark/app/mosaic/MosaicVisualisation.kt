package com.bumble.appyx.benchmark.app.mosaic

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.times
import com.bumble.appyx.benchmark.app.mosaic.MosaicModel.MosaicMode.ASSEMBLED
import com.bumble.appyx.benchmark.app.mosaic.MosaicModel.MosaicMode.CAROUSEL
import com.bumble.appyx.benchmark.app.mosaic.MosaicModel.MosaicMode.FLIPPED
import com.bumble.appyx.benchmark.app.mosaic.MosaicModel.MosaicMode.SCATTERED
import com.bumble.appyx.benchmark.app.mosaic.MosaicModel.State
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.math.smoothstep
import com.bumble.appyx.interactions.core.ui.property.impl.AngularPosition
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.RoundedCorners
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.Center
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.fractionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOffset
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation
import kotlin.math.min
import kotlin.random.Random

@Suppress("MagicNumber")
class MosaicVisualisation(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float>
) : BaseVisualisation<MosaicPiece, State, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec
) {
    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableUiState(uiContext)

    override fun State.toUiTargets(): List<MatchedTargetUiState<MosaicPiece, TargetUiState>> =
        pieces.mapIndexed { idx, piece ->
            val (i, j) = piece.interactionTarget
            MatchedTargetUiState(
                element = piece,
                targetUiState = when (mosaicMode) {
                    SCATTERED -> scattered(i, j)
                    ASSEMBLED -> assembled(i, j, idx)
                    FLIPPED -> flipped(i, j, idx)
                    CAROUSEL -> carousel(i, j, idx)
                }
            )
        }

    private fun State.scattered(i: Int, j: Int) = TargetUiState(
        position = PositionAlignment.Target(
            insideAlignment = alignment(i, j)
        ),
        positionOffset = PositionOffset.Target(
            DpOffset(
                x = offset(i, gridCols, transitionBounds.widthDp),
                y = offset(j, gridRows, transitionBounds.heightDp),
            ),
        ),
        rotationZ = RotationZ.Target(
            (if (Random.nextBoolean()) -1 else 1) * Random.nextInt(1, 4) * 360f
        )
    )

    private fun offset(index: Int, maxIndex: Int, step: Dp): Dp {
        var multiplier = (index - (maxIndex - 1) / 2f)
        // if maxIndex is odd the middle one will always have 0 offsetX without this check
        if (multiplier == 0f) {
            multiplier = 1f
        }
        return multiplier * Random.nextInt(3, 9) * 0.5f * step
    }

    private fun State.assembled(i: Int, j: Int, idx: Int) = TargetUiState(
        position = PositionAlignment.Target(
            insideAlignment = alignment(i, j),
        ),
        angularPosition = AngularPosition.Target(
            AngularPosition.Value(
                // Prepares angle as we only want to animate radius later
                angleDegrees = angle(idx),
                // Since radius is zero, angle won't just just yet
                radius = 0f,
            )
        )
    )

    private fun State.flipped(i: Int, j: Int, idx: Int) =
        assembled(i, j, idx).copy(
            rotationY = RotationY.Target(180f, easing = gridEasing(i, j)),
        )

    private fun State.carousel(i: Int, j: Int, idx: Int): TargetUiState {
        val flipped = flipped(i, j, idx)
        val maxRings = pieces.size / 30 + 1
        val targetRing = idx % maxRings + 2

        return flipped.copy(
            position = PositionAlignment.Target(Center),
            rotationZ = RotationZ.Target(
                (if (Random.nextBoolean()) -1 else 1) * Random.nextInt(1, 3) * 360f
            ),
            angularPosition = AngularPosition.Target(
                AngularPosition.Value(
                    // This should be the same as the prepared angle in the assembled state
                    // as it's not supposed to be animated
                    angleDegrees = angle(idx),
                    // This will animate radius from 0 to a new value
                    radius = targetRing * 0.15f * min(
                        transitionBounds.widthDp.value,
                        transitionBounds.heightDp.value
                    )
                )
            ),
            roundedCorners = RoundedCorners.Target(4),
        )
    }

    private fun State.angle(idx: Int) =
        360f * (idx / pieces.size.toFloat())

    /**
     * Calculates easing for a single element in the grid such that:
     * - The [Easing] maps inputs of 0..1f to another range of 0..1f,
     * - Transitioning 0 -> 1 in our case is achieved with [smoothstep]:
     *      - https://en.wikipedia.org/wiki/Smoothstep
     * - Initially the easing stays on 0f for a delayed time.
     *      - This is the first parameter passed to the [smoothstep] function
     *      - This is calculated based on the element's position in the grid (i, j).
     * - After reaching 1, it stays there.
     *      - When the easing reaches 1f is determined by the second parameter
     *        passed to [smoothstep].
     *
     * The entire range is always 0..1f (the total time is determined by the tween itself,
     * not this number);
     * The more elements we have the smaller proportion of this range will be allocated to each.
     * We need to slice up the range to units proportional to the total number of columns:
     *
     * val unit = 1f / gridCols
     *
     * Additionally we want some amount of overlap in animations:
     * x x x x x
     *   x x x x x
     *     x x x x x
     *       x x x x x
     *         x x x x x
     *
     * Where x = 1 unit, and the counts of x is the overlap value.
     * This gives us an animation length = overlap * unit for one element.
     *
     * Lastly, we calculate a startIdx to determine the element's starting point by:
     * - The more it is to the right (value of i) the more it is delayed
     * - The ore it is to the bottom (value of j) the more it is delayed
     *
     * And because we have some delay for finishing the animation of a individual element (both
     * coming from overlap size and their rows), this is accounted for in the unit size too.
     */
    private fun State.gridEasing(i: Int, j: Int): Easing = Easing { fraction ->
        val overlap = 5
        val unit = 1f / (gridCols + overlap + gridRows)
        val length = overlap * unit
        val startIdx = (i + j * 0.5f)

        smoothstep(
            // When to begin transitioning towards 1f
            startIdx * unit,
            // When to reach 1f
            startIdx * unit + length,
            // The received input of 0..1 of the overall transition progress
            fraction
        )
    }

    private fun State.alignment(
        i: Int,
        j: Int
    ) = fractionAlignment(
        horizontalBiasFraction = i * (1f / (gridCols - 1)),
        verticalBiasFraction = j * (1f / (gridRows - 1))
    )
}
