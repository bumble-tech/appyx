package com.bumble.appyx.transitionmodel.backstack

import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.core.TransitionModel.Output.Segment
import com.bumble.appyx.interactions.core.TransitionModel.Output.Update
import com.bumble.appyx.interactions.core.ui.FrameModel
import com.bumble.appyx.interactions.core.ui.Interpolator
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import kotlinx.coroutines.CoroutineScope
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
        listOf(
            MatchedProps(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedProps(it, hidden)
        }

    override fun mapUpdate(update: Update<BackStackModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val targetProps = update.targetState.toProps()

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val elementProps = interpolated.getOrPut(t1.element.id) {
                Props(alpha = Alpha(0f))
            }

            Logger.log("FRAME", "Produced target: ${t1.element.navTarget} id=${t1.element.id} value=${elementProps.alpha.value} goal=${t1.props.alpha.value}")
            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier
                    .composed {
                        LaunchedEffect(elementProps.alpha.animatable.asState().value) {
                            Logger.log("Alpha", "${t1.element.navTarget} value = ${elementProps.alpha.animatable.asState().value}")
                        }
                        this
                    }
                    .composed {
                        LaunchedEffect(update) {
                            if (elementProps.alpha.value != t1.props.alpha.value) {
                                Logger.log("Animate", "Animation launched target=${t1.element.navTarget} id=${t1.element.id} value=${elementProps.alpha.value}")
                                elementProps.animateTo(t1.props)
                                Logger.log("Animate", "Animation finished target=${t1.element.navTarget} id=${t1.element.id} value=${elementProps.alpha.value}")
                            }
                        }
                        this
                    },
                progress = 1f,
                props = elementProps
            )
        }
    }

    override fun mapSegment(segment: Segment<BackStackModel.State<NavTarget>>): List<FrameModel<NavTarget>> {
        val (fromState, targetState) = segment.navTransition
        val fromProps = fromState.toProps()
        val targetProps = targetState.toProps()

        // TODO: use a map instead of find
        return targetProps.map { t1 ->
            val t0 = fromProps.find { it.element.id == t1.element.id }!!
            val elementProps = interpolated.getOrPut(t1.element.id) {
                Props(alpha = Alpha(0f))
            }

            runBlocking {
                elementProps.lerpTo(t0.props, t1.props, segment.progress)
            }

            Logger.log("FRAME", "frame produced target=${t1.element.navTarget} id=${t1.element.id} value=${elementProps.alpha.value} -> ${t1.props.alpha.value}")
            Logger.log("FRAME", "${segment.navTransition.targetState}")
            FrameModel(
                navElement = t1.element,
                modifier = elementProps.modifier
                    .composed {
                        LaunchedEffect(elementProps.alpha.animatable.asState().value) {
                            Logger.log("ALPHA", "${t1.element.navTarget} value=${elementProps.alpha.animatable.asState().value}")
                        }
                        this
                    },
                progress = segment.progress,
                props = elementProps
            )
        }
    }
}
