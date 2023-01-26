package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpDpOffset
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.TransitionBounds
import com.bumble.appyx.interactions.core.ui.VisibilityInterpolator
import com.bumble.appyx.transitionmodel.backstack.BackStackModel

class BackStackSlider<NavTarget>(
    transitionBounds: TransitionBounds
) : Interpolator<NavTarget, BackStackModel.State<NavTarget>>,
    VisibilityInterpolator<NavTarget, BackStackModel.State<NavTarget>> by BackStackVisibilityInterpolator() {
    private val width = transitionBounds.widthDp

    data class Props(
        val offset: DpOffset,
        val offsetMultiplier: Int = 1,
        val alpha: Float = 1f
    )

    private val outsideLeft = Props(
        offset = DpOffset(-width, 0.dp)
    )

    private val outsideRight = Props(
        offset = DpOffset(width, 0.dp)
    )

    private val noOffset = Props(
        offset = DpOffset(0.dp, 0.dp)
    )

    private fun <NavTarget> BackStackModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        created.map { MatchedProps(it, outsideRight) } +
                listOf(MatchedProps(active, noOffset)) +
                stashed.mapIndexed { index, navElement ->
                    MatchedProps(
                        navElement,
                        outsideLeft.copy(offsetMultiplier = index + 1)
                    )
                } +
                destroyed.map { navElement ->
                    MatchedProps(
                        navElement,
                        outsideRight.copy(alpha = 0f)
                    )
                }

    override fun map(segment: TransitionModel.Segment<BackStackModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!

            val offset = lerpDpOffset(
                start = t0.props.offset,
                end = t1.props.offset,
                progress = segment.progress
            )

            val alpha = lerpFloat(
                start = t0.props.alpha,
                end = t1.props.alpha,
                progress = segment.progress
            )

            FrameModel(
                navElement = t1.element,
                modifier = Modifier
                    .alpha(alpha)
                    .offset(
                        x = offset.x,
                        y = offset.y
                    ),
                progress = segment.progress
            )
        }

    }

    operator fun DpOffset.times(multiplier: Int) =
        DpOffset(x * multiplier, y * multiplier)

}
