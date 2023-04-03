package com.bumble.appyx.transitionmodel.testdrive.interpolator

import DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.state.BaseUiState
import com.bumble.appyx.interactions.core.ui.state.MatchedUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.B
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.C
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.D
import com.bumble.appyx.transitionmodel.testdrive.interpolator.TestDriveMotionController.UiState
import com.bumble.appyx.transitionmodel.testdrive.operation.MoveTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.math.abs

class TestDriveMotionController<InteractionTarget : Any>(
    private val uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<InteractionTarget, TestDriveModel.State<InteractionTarget>, UiState>(
    uiContext = uiContext,
    defaultAnimationSpec = uiAnimationSpec,
) {
    override fun defaultUiState(uiContext: UiContext, initialUiState: UiState?): UiState = UiState(uiContext)

    // TODO extract
    class UiState(
        val uiContext: UiContext,
        val position: Position = Position(
            initialOffset = DpOffset.Zero,
            clipToBounds = uiContext.clipToBounds
        ),
        val backgroundColor: BackgroundColor = BackgroundColor(md_red_500),
    ) : BaseUiState<UiState>(
        motionProperties = listOf(position, backgroundColor),
        coroutineScope = uiContext.coroutineScope
    ) {

        override val modifier: Modifier
            get() = Modifier
                .then(position.modifier)
                .then(backgroundColor.modifier)

        override suspend fun snapTo(scope: CoroutineScope, uiState: UiState) {
            scope.launch {
                position.snapTo(uiState.position.value)
                backgroundColor.snapTo(uiState.backgroundColor.value)
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            uiState: UiState,
            springSpec: SpringSpec<Float>,
        ) {
            listOf(
                scope.async {
                    position.animateTo(
                        uiState.position.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                },
                scope.async {
                    backgroundColor.animateTo(
                        uiState.backgroundColor.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    )
                }
            ).awaitAll()
        }

        override fun lerpTo(scope: CoroutineScope, start: UiState, end: UiState, fraction: Float) {
            scope.launch {
                position.lerpTo(start.position, end.position, fraction)
                backgroundColor.lerpTo(start.backgroundColor, end.backgroundColor, fraction)
            }
        }
    }

    companion object {
        val offsetA = DpOffset(0.dp, 0.dp)
        val offsetB = DpOffset(200.dp, 0.dp)
        val offsetC = DpOffset(200.dp, 300.dp)
        val offsetD = DpOffset(0.dp, 300.dp)

        fun TestDriveModel.State.ElementState.toUiState(uiContext: UiContext): UiState =
            when (this) {
                A -> UiState(
                    uiContext = uiContext,
                    position = Position(
                        initialOffset = offsetA,
                        clipToBounds = uiContext.clipToBounds
                    ),
                    backgroundColor = BackgroundColor(md_red_500)
                )
                B -> UiState(
                    uiContext = uiContext,
                    position = Position(
                        initialOffset = offsetB,
                        clipToBounds = uiContext.clipToBounds
                    ),
                    backgroundColor = BackgroundColor(md_light_green_500)
                )
                C -> UiState(
                    uiContext = uiContext,
                    position = Position(
                        initialOffset = offsetC,
                        clipToBounds = uiContext.clipToBounds
                    ),
                    backgroundColor = BackgroundColor(md_yellow_500)
                )
                D -> UiState(
                    uiContext = uiContext,
                    position = Position(
                        initialOffset = offsetD,
                        clipToBounds = uiContext.clipToBounds
                    ),
                    backgroundColor = BackgroundColor(md_light_blue_500)
                )
            }
    }

    override fun TestDriveModel.State<InteractionTarget>.toUiState(): List<MatchedUiState<InteractionTarget, UiState>> =
        listOf(
            MatchedUiState(element, elementState.toUiState(uiContext)).also {
                Logger.log("TestDrive", "Matched $elementState -> UiState: ${it.uiState}")
            }
        )

    class Gestures<InteractionTarget>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
        private val width = offsetB.x - offsetA.x
        private val height = offsetD.y - offsetA.y

        override fun createGesture(
            delta: androidx.compose.ui.geometry.Offset,
            density: Density
        ): Gesture<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
            val width = with(density) { width.toPx() }
            val height = with(density) { height.toPx() }

            return if (abs(delta.x) > abs(delta.y)) {
                if (delta.x < 0) {
                    Gesture(
                        operation = MoveTo(D),
                        dragToProgress = { offset -> (offset.x / width) * -1 },
                        partial = { offset, progress -> offset.copy(x = progress * width * -1) }
                    )
                } else {
                    Gesture(
                        operation = MoveTo(B),
                        dragToProgress = { offset -> (offset.x / width) },
                        partial = { offset, partial -> offset.copy(x = partial * width) }
                    )
                }
            } else {
                if (delta.y < 0) {
                    Gesture(
                        operation = MoveTo(A),
                        dragToProgress = { offset -> (offset.y / height) * -1 },
                        partial = { offset, partial -> offset.copy(y = partial * height * -1) }
                    )
                } else {
                    Gesture(
                        operation = MoveTo(C),
                        dragToProgress = { offset -> (offset.y / height) },
                        partial = { offset, partial -> offset.copy(y = partial * height) }
                    )
                }
            }
        }
    }
}

