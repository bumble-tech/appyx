package com.bumble.appyx.transitionmodel.testdrive.interpolator

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.impl.BackgroundColor
import com.bumble.appyx.interactions.core.ui.property.impl.Offset
import com.bumble.appyx.transitionmodel.BaseInterpolator
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class TestDriveUiModel<NavTarget : Any>(
    transitionBounds: TransitionBounds
) : BaseInterpolator<NavTarget, TestDriveModel.State<NavTarget>, TestDriveUiModel.Props>({ Props() }) {

    class Props(
        val offset: Offset = Offset(DpOffset(0.dp, 0.dp)),
        val backgroundColor: BackgroundColor = BackgroundColor(md_red_500),
        override val isVisible: Boolean = true
    ) : Interpolatable<Props>, HasModifier, BaseProps, Animatable<Props> {

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            offset.lerpTo(start.offset, end.offset, fraction)
            backgroundColor.lerpTo(start.backgroundColor, end.backgroundColor, fraction)
        }

        override val modifier: Modifier
            get() = Modifier
                .then(offset.modifier)
                .then(backgroundColor.modifier)

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: Props,
            onStart: () -> Unit,
            onFinished: () -> Unit
        ) {
            // FIXME this should match the own animationSpec of the model (which can also be supplied
            //  from operation extension methods) rather than created here
            val animationSpec: SpringSpec<Float> = spring(
                stiffness = Spring.StiffnessVeryLow / 5,
                dampingRatio = Spring.DampingRatioLowBouncy,
            )
            onStart()
            listOf(
                scope.async {
                    offset.animateTo(
                        props.offset.value,
                        spring(animationSpec.dampingRatio, animationSpec.stiffness)
                    )
                },
                scope.async {
                    backgroundColor.animateTo(
                        props.backgroundColor.value,
                        spring(animationSpec.dampingRatio, animationSpec.stiffness)
                    )
                }
            ).awaitAll()
            onFinished()
        }
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
}

