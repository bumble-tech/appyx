@file:Suppress("MagicNumber")
package com.bumble.appyx.components.experimental.puzzle15.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.components.experimental.puzzle15.Puzzle15Model
import com.bumble.appyx.components.experimental.puzzle15.Puzzle15Model.Tile
import com.bumble.appyx.components.experimental.puzzle15.operation.Swap
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Drag
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragDirection4
import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.fractionAlignment
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionAlignment
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseVisualisation

class Puzzle15Visualisation(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseVisualisation<Tile, Puzzle15Model.State, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec
) {
    companion object {
        private const val CELLS_COUNT = 4
    }

    override fun Puzzle15Model.State.toUiTargets(): List<MatchedTargetUiState<Tile, TargetUiState>> {
        return items.mapIndexed { index, tileElements ->
            MatchedTargetUiState(
                element = tileElements,
                targetUiState = TargetUiState(
                    positionAlignment = PositionAlignment.Target(
                        insideAlignment = fractionAlignment(
                            horizontalBiasFraction = (index % CELLS_COUNT).toFloat() / (CELLS_COUNT - 1),
                            verticalBiasFraction = (index / CELLS_COUNT).toFloat() / (CELLS_COUNT - 1)
                        )
                    )
                )
            )
        }
    }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableUiState(uiContext)

    class Gestures(
        bounds: TransitionBounds,
    ) : GestureFactory<Tile, Puzzle15Model.State> {

        override val isContinuous: Boolean = false

        private val cellSize: Float = bounds.widthPx / 4f

        override fun createGesture(
            state: Puzzle15Model.State,
            delta: Offset,
            density: Density
        ): Gesture<Tile, Puzzle15Model.State> {
            return when (dragDirection4(delta)) {
                Drag.Direction4.UP -> Gesture(
                    operation = Swap(Swap.Direction.DOWN),
                    completeAt = Offset(0f, -cellSize),
                )

                Drag.Direction4.LEFT -> Gesture(
                    operation = Swap(Swap.Direction.RIGHT),
                    completeAt = Offset(-cellSize, 0f),
                )

                Drag.Direction4.RIGHT -> Gesture(
                    operation = Swap(Swap.Direction.LEFT),
                    completeAt = Offset(cellSize, 0f),
                )

                Drag.Direction4.DOWN -> Gesture(
                    operation = Swap(Swap.Direction.UP),
                    completeAt = Offset(0f, cellSize),
                )

            }
        }
    }
}
