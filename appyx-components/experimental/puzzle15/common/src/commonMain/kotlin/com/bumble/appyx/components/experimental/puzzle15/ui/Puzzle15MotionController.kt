package com.bumble.appyx.components.experimental.puzzle15.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
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
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class Puzzle15MotionController(
    private val uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<Tile, Puzzle15Model.State, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec
) {

    override fun Puzzle15Model.State.toUiTargets(): List<MatchedTargetUiState<Tile, TargetUiState>> {
        val width = uiContext.transitionBounds.widthDp.value / 4
        return items.mapIndexed { index, tileElements ->
            MatchedTargetUiState(
                element = tileElements,
                targetUiState = TargetUiState(
                    position = Position.Target(
                        offset = DpOffset(
                            x = (index % 4 * width).dp,
                            y = (index / 4 * width).dp
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
        targetUiState.toMutableState(uiContext)

    class Gestures(
        bounds: TransitionBounds,
    ) : GestureFactory<Tile, Puzzle15Model.State> {

        private val cellSize: Float = bounds.widthPx / 4f

        override fun createGesture(
            state: Puzzle15Model.State,
            delta: Offset,
            density: Density
        ): Gesture<Tile, Puzzle15Model.State> {
            return when (dragDirection4(delta)) {
                Drag.Direction4.UP -> Gesture(
                    operation = Swap(Swap.Direction.DOWN),
                    completeAt = Offset(0f, -cellSize)
                )

                Drag.Direction4.LEFT -> Gesture(
                    operation = Swap(Swap.Direction.RIGHT),
                    completeAt = Offset(-cellSize, 0f)
                )

                Drag.Direction4.RIGHT -> Gesture(
                    operation = Swap(Swap.Direction.LEFT),
                    completeAt = Offset(cellSize, 0f)
                )

                Drag.Direction4.DOWN -> Gesture(
                    operation = Swap(Swap.Direction.UP),
                    completeAt = Offset(0f, cellSize)
                )

            }
        }
    }
}
