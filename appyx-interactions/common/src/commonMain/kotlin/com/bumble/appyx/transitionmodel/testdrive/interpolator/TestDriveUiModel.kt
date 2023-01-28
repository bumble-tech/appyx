package com.bumble.appyx.transitionmodel.testdrive.interpolator

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Offset
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.B
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.C
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TestDriveUiModel<NavTarget : Any>(
    transitionBounds: TransitionBounds
) : Interpolator<NavTarget, TestDriveModel.State<NavTarget>> {

    class Props(
        val offset: Offset = Offset(DpOffset(0.dp, 0.dp)),
        val backgroundColor: BackgroundColor = BackgroundColor(md_red_500)
    ) : Interpolatable<Props>, HasModifier {

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            offset.lerpTo(start.offset, end.offset, fraction)
            backgroundColor.lerpTo(start.backgroundColor, end.backgroundColor, fraction)
        }

        override val modifier: Modifier
            get() = Modifier
                .then(offset.modifier)
                .then(backgroundColor.modifier)

        suspend fun animateTo(scope: CoroutineScope, props: Props) {
            // FIXME this should match the own animationSpec of the model (which can also be supplied
            //  from operation extension methods) rather than created here
            val animationSpec: SpringSpec<Float> = spring(
                stiffness = Spring.StiffnessVeryLow,
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
            scope.launch {
                offset.animateTo(props.offset.value, spring(animationSpec.dampingRatio, animationSpec.stiffness))
            }
            scope.launch {
                backgroundColor.animateTo(props.backgroundColor.value, spring(animationSpec.dampingRatio, animationSpec.stiffness))
            }
        }
    }

    private val a = Props(
        offset = Offset(DpOffset(0.dp, 0.dp)),
        backgroundColor = BackgroundColor(md_red_500)
    )

    private val b = Props(
        offset = Offset(DpOffset(200.dp, 0.dp)),
        backgroundColor = BackgroundColor(md_light_green_500)
    )

    private val c = Props(
        offset = Offset(DpOffset(200.dp, 200.dp)),
        backgroundColor = BackgroundColor(md_yellow_500)
    )

    private val d = Props(
        offset = Offset(DpOffset(0.dp, 200.dp)),
        backgroundColor = BackgroundColor(md_light_blue_500)
    )

    private fun <NavTarget : Any> TestDriveModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        listOf(
            MatchedProps(element, when(elementState) {
                A -> a
                B -> b
                C -> c
                D -> d
            }).also {
                Logger.log("TestDrive", "Matched $elementState -> Props: ${it.props}")
            }
        )

    private val cache: MutableMap<String, Props> = mutableMapOf()

    override fun mapUpdate(update: TransitionModel.Output.Update<TestDriveModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val targetProps = update.targetState.toProps()

        return targetProps.map { t1 ->
            val elementProps = cache.getOrPut(t1.element.id) { Props() }

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier
                    .composed {
                        LaunchedEffect(t1.props) {
                            Logger.log("TestDrive", "Animating ${t1.element} to ${t1.props}")
                            elementProps.animateTo(this, t1.props)
                        }
                        this
                    },
                progress = 1f
            )
        }
    }

    override fun mapSegment(segment: TransitionModel.Output.Segment<TestDriveModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementProps = cache.getOrPut(t1.element.id) { Props() }

            runBlocking {
                elementProps.lerpTo(t0.props, t1.props, segment.progress)
            }

            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier
                    .composed { this },
                progress = segment.progress
            )
        }
    }
}

