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
import com.bumble.appyx.interactions.core.ui.output.BaseUiState
import com.bumble.appyx.interactions.core.ui.output.MatchedUiState
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Offset
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.B
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.C
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.D
import com.bumble.appyx.transitionmodel.testdrive.operation.MoveTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.math.abs

class TestDriveUiModel<InteractionTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseMotionController<InteractionTarget, TestDriveModel.State<InteractionTarget>, TestDriveUiModel.UiState>(
    scope = uiContext.coroutineScope,
    defaultAnimationSpec = uiAnimationSpec,
) {
    override fun defaultUiState(): UiState = UiState()

    // TODO extract
    class UiState(
        val offset: Offset = Offset(DpOffset(0.dp, 0.dp)),
        val backgroundColor: BackgroundColor = BackgroundColor(md_red_500),
    ) : HasModifier, BaseUiState(listOf(offset.isAnimating, backgroundColor.isAnimating)), Animatable<UiState> {

        override val modifier: Modifier
            get() = Modifier
                .then(offset.modifier)
                .then(backgroundColor.modifier)

        override suspend fun snapTo(scope: CoroutineScope, uiState: UiState) {
            scope.launch {
                offset.snapTo(uiState.offset.value)
                backgroundColor.snapTo(uiState.backgroundColor.value)
                updateVisibilityState()
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            uiState: UiState,
            springSpec: SpringSpec<Float>,
        ) {
            listOf(
                scope.async {
                    offset.animateTo(
                        uiState.offset.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    ) {
                        updateVisibilityState()
                    }
                },
                scope.async {
                    backgroundColor.animateTo(
                        uiState.backgroundColor.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    ) {
                        updateVisibilityState()
                    }
                }
            ).awaitAll()
        }

        override fun lerpTo(scope: CoroutineScope, start: UiState, end: UiState, fraction: Float) {
            scope.launch {
                offset.lerpTo(start.offset, end.offset, fraction)
                backgroundColor.lerpTo(start.backgroundColor, end.backgroundColor, fraction)
                updateVisibilityState()
            }
        }

        override fun isVisible() = true
    }

    companion object {
        fun TestDriveModel.State.ElementState.toUiState(): UiState =
            when (this) {
                A -> a
                B -> b
                C -> c
                D -> d
            }

        val a = UiState(
            offset = Offset(DpOffset(0.dp, 0.dp)),
            backgroundColor = BackgroundColor(md_red_500)
        )

        val b = UiState(
            offset = Offset(DpOffset(200.dp, 0.dp)),
            backgroundColor = BackgroundColor(md_light_green_500)
        )

        val c = UiState(
            offset = Offset(DpOffset(200.dp, 300.dp)),
            backgroundColor = BackgroundColor(md_yellow_500)
        )

        val d = UiState(
            offset = Offset(DpOffset(0.dp, 300.dp)),
            backgroundColor = BackgroundColor(md_light_blue_500)
        )
    }

    override fun TestDriveModel.State<InteractionTarget>.toUiState(): List<MatchedUiState<InteractionTarget, UiState>> =
        listOf(
            MatchedUiState(element, elementState.toUiState()).also {
                Logger.log("TestDrive", "Matched $elementState -> UiState: ${it.uiState}")
            }
        )

    class Gestures<InteractionTarget>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, TestDriveModel.State<InteractionTarget>> {
        private val width = b.offset.value.x - a.offset.value.x
        private val height = d.offset.value.y - a.offset.value.y

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

