package com.bumble.appyx.components.demos.puzzle15.ui

import DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.demos.puzzle15.Puzzle15Model
import com.bumble.appyx.components.demos.puzzle15.Puzzle15Model.Tile
import com.bumble.appyx.components.demos.puzzle15.operation.Swap
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import kotlin.math.abs

class Puzzle15MotionController(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<Tile, Puzzle15Model.State, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = defaultAnimationSpec
) {

    override fun Puzzle15Model.State.toUiTargets(): List<MatchedTargetUiState<Tile, TargetUiState>> =
        items.mapIndexed { index, tileElements ->
            MatchedTargetUiState(
                element = tileElements, // todo: if i create items here this misbehaves because the ids will be different
                targetUiState = TargetUiState(
                    position = Position.Target(
                        value = DpOffset(
                            x = (index % 4 * 60).dp,
                            y = (index / 4 * 60).dp
                        )
                    )
                )
            )
        }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext)

    class Gestures : GestureFactory<Tile, Puzzle15Model.State> {
        override fun createGesture(
            delta: Offset,
            density: Density
        ): Gesture<Tile, Puzzle15Model.State> {
            val distance = with(density) { 60.dp.toPx() }
            return if (abs(delta.x) > abs(delta.y)) {
                if (delta.x < 0) {
                    Gesture(
                        operation = Swap(direction = Swap.Direction.Right),
                        dragToProgress = { offset -> (offset.x / distance) * -1 },
                        partial = { offset, progress -> offset.copy(x = progress * distance * -1) }
                    )
                } else {
                    Gesture(
                        operation = Swap(direction = Swap.Direction.Left),
                        dragToProgress = { offset -> (offset.x / distance) },
                        partial = { offset, partial -> offset.copy(x = partial * distance) }
                    )
                }
            } else {
                if (delta.y < 0) {
                    Gesture(
                        operation = Swap(direction = Swap.Direction.Down),
                        dragToProgress = { offset -> (offset.y / distance) * -1 },
                        partial = { offset, partial -> offset.copy(y = partial * distance * -1) }
                    )
                } else {
                    Gesture(
                        operation = Swap(direction = Swap.Direction.Up),
                        dragToProgress = { offset -> (offset.y / distance) },
                        partial = { offset, partial -> offset.copy(y = partial * distance) }
                    )
                }
            }

        }
    }
}
