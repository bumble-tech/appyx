package com.bumble.appyx.transitionmodel.backstack

import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BackstackFader<NavTarget : Any>(
    private val scope: CoroutineScope
) : Interpolator<NavTarget, BackStackModel.State<NavTarget>> {

    class Props(
        var alpha: Alpha
    ) : Interpolatable<Props>, HasModifier {

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            alpha.lerpTo(start.alpha, end.alpha, fraction)
        }

        override val modifier: Modifier
            get() = Modifier
                .then(alpha.modifier)

        suspend fun animateTo(props: Props) {
            alpha.animateTo(props.alpha.value, spring())
        }
    }

    private val visible = Props(
        alpha = Alpha(1f)
    )

    private val hidden = Props(
        alpha = Alpha(0f)
    )

    private val interpolated: MutableMap<String, Props> = mutableMapOf()

    private fun <NavTarget : Any> BackStackModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        listOf(MatchedProps(active, visible)) + (created + destroyed + stashed).map {
            MatchedProps(it, hidden)
        }

    override fun map(segment: TransitionModel.Segment<BackStackModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementProps = interpolated.getOrPut(t0.element.id) { t0.props }
            // TODO

            if (segment.animate) {
                scope.launch {
                    elementProps.animateTo(t1.props)
                }
            } else {
                runBlocking {
                    elementProps.lerpTo(t0.props, t1.props, segment.progress)
                }
            }


            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier,
                progress = segment.progress
            )
        }
    }
}
