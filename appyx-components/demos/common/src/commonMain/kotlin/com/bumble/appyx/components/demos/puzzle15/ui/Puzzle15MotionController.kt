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
import com.bumble.appyx.interactions.core.ui.gesture.Drag
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragDirection4
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

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
            state: Puzzle15Model.State,
            delta: Offset,
            density: Density
        ): Gesture<Tile, Puzzle15Model.State> {
            val distance = with(density) { 60.dp.toPx() }
            return when (dragDirection4(delta)) {
                Drag.Direction4.UP -> Gesture(
                    operation = Swap(Swap.Direction.Down),
                    completeAt = Offset(0f, -distance)
                )

                Drag.Direction4.LEFT -> Gesture(
                    operation = Swap(Swap.Direction.Right),
                    completeAt = Offset(-distance, 0f)
                )

                Drag.Direction4.RIGHT -> Gesture(
                    operation = Swap(Swap.Direction.Left),
                    completeAt = Offset(distance, 0f)
                )

                Drag.Direction4.DOWN -> Gesture(
                    operation = Swap(Swap.Direction.Up),
                    completeAt = Offset(0f, distance)
                )

            }
        }
    }
}
