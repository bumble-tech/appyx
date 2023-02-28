package com.bumble.appyx.transitionmodel.testdrive.interpolator

import DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.inputsource.Gesture
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.GestureFactory
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.UiContext
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Offset
import com.bumble.appyx.transitionmodel.BaseInterpolator
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

class TestDriveUiModel<NavTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseInterpolator<NavTarget, TestDriveModel.State<NavTarget>, TestDriveUiModel.Props>(
    scope = uiContext.coroutineScope,
    defaultAnimationSpec = uiAnimationSpec,
) {
    override fun defaultProps(): Props = Props()

    class Props(
        val offset: Offset = Offset(DpOffset(0.dp, 0.dp)),
        val backgroundColor: BackgroundColor = BackgroundColor(md_red_500),
    ) : HasModifier, BaseProps(listOf(offset.isAnimating, backgroundColor.isAnimating)), Animatable<Props> {

        override val modifier: Modifier
            get() = Modifier
                .then(offset.modifier)
                .then(backgroundColor.modifier)

        override suspend fun snapTo(scope: CoroutineScope, props: Props) {
            scope.launch {
                offset.snapTo(props.offset.value)
                backgroundColor.snapTo(props.backgroundColor.value)
                updateVisibilityState()
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: Props,
            springSpec: SpringSpec<Float>,
        ) {
            listOf(
                scope.async {
                    offset.animateTo(
                        props.offset.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    ) {
                        updateVisibilityState()
                    }
                },
                scope.async {
                    backgroundColor.animateTo(
                        props.backgroundColor.value,
                        spring(springSpec.dampingRatio, springSpec.stiffness)
                    ) {
                        updateVisibilityState()
                    }
                }
            ).awaitAll()
        }

        override fun lerpTo(scope: CoroutineScope, start: Props, end: Props, fraction: Float) {
            scope.launch {
                offset.lerpTo(start.offset, end.offset, fraction)
                backgroundColor.lerpTo(start.backgroundColor, end.backgroundColor, fraction)
                updateVisibilityState()
            }
        }

        override fun isVisible() = true
    }

    companion object {
        fun TestDriveModel.State.ElementState.toProps(): Props =
            when (this) {
                A -> a
                B -> b
                C -> c
                D -> d
            }

        val a = Props(
            offset = Offset(DpOffset(0.dp, 0.dp)),
            backgroundColor = BackgroundColor(md_red_500)
        )

        val b = Props(
            offset = Offset(DpOffset(200.dp, 0.dp)),
            backgroundColor = BackgroundColor(md_light_green_500)
        )

        val c = Props(
            offset = Offset(DpOffset(200.dp, 300.dp)),
            backgroundColor = BackgroundColor(md_yellow_500)
        )

        val d = Props(
            offset = Offset(DpOffset(0.dp, 300.dp)),
            backgroundColor = BackgroundColor(md_light_blue_500)
        )
    }

    override fun TestDriveModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        listOf(
            MatchedProps(element, elementState.toProps()).also {
                Logger.log("TestDrive", "Matched $elementState -> Props: ${it.props}")
            }
        )

    class Gestures<NavTarget>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<NavTarget, TestDriveModel.State<NavTarget>> {
        private val width = b.offset.value.x - a.offset.value.x
        private val height = d.offset.value.y - a.offset.value.y

        override fun createGesture(
            delta: androidx.compose.ui.geometry.Offset,
            density: Density
        ): Gesture<NavTarget, TestDriveModel.State<NavTarget>> {
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

