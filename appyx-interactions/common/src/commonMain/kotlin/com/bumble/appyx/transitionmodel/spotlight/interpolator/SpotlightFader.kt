package com.bumble.appyx.transitionmodel.spotlight.interpolator

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.TransitionParams
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.Interpolator.Companion.lerpFloat
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SpotlightFader<NavTarget>(
    private val scope: CoroutineScope
) : Interpolator<NavTarget, SpotlightModel.State> {

    class Props(
        var alpha: Alpha
    ) : Interpolatable<Props>, HasModifier {

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            alpha.lerpTo(start.alpha, end.alpha, fraction)
        }

        override val modifier: Modifier
            get() = Modifier
                .then(alpha.modifier)
    }

    private val visible = Props(
        alpha = Alpha(1f)
    )

    private val hidden = Props(
        alpha = Alpha(0f)
    )

    private val interpolated = Props(
        alpha = Alpha(0f)
    )

    private fun SpotlightModel.State.toProps(): Props =
        when (this) {
            SpotlightModel.State.ACTIVE -> visible
            else -> hidden
        }

    override fun map(segment: TransitionModel.Segment<NavTarget, SpotlightModel.State>): List<FrameModel<NavTarget, SpotlightModel.State>> {
        val (fromState, targetState) = segment.navTransition

        return targetState.map { t1 ->
            val t0 = fromState.find { it.key == t1.key }!!

            val fromProps = t0.state.toProps()
            val targetProps = t1.state.toProps()

            // TODO
            scope.launch {
                interpolated.lerpTo(fromProps, targetProps, segment.progress)
            }

            FrameModel(
                navElement = t1,
                modifier = interpolated.modifier,
                progress = segment.progress
            )
        }
    }
}
