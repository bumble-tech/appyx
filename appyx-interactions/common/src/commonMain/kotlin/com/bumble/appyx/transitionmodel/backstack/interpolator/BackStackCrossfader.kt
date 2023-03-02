package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.transitionmodel.BaseMotionController
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BackStackCrossfader<InteractionTarget : Any>(
    scope: CoroutineScope
) : BaseMotionController<InteractionTarget, BackStackModel.State<InteractionTarget>, BackStackCrossfader.Props>(
    scope = scope
) {

    override fun defaultProps(): Props = Props()

    class Props(
        val alpha: Alpha = Alpha(value = 1f),
    ) : BaseProps(listOf(alpha.isAnimating)), HasModifier, Animatable<Props> {

        override val modifier: Modifier
            get() = Modifier
                .then(alpha.modifier)

        override suspend fun snapTo(scope: CoroutineScope, props: Props) {
            scope.launch {
                alpha.snapTo(props.alpha.value)
                updateVisibilityState()
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: Props,
            springSpec: SpringSpec<Float>,
        ) {
            val a1 = scope.async {
                alpha.animateTo(
                    props.alpha.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness)
                ) {
                    updateVisibilityState()
                }
            }
            a1.await()
        }

        override fun lerpTo(scope: CoroutineScope, start: Props, end: Props, fraction: Float) {
            scope.launch {
                alpha.lerpTo(start.alpha, end.alpha, fraction)
                updateVisibilityState()
            }
        }

        override fun isVisible() = alpha.value > 0.0f

    }

    private val visible = Props(
        alpha = Alpha(value = 1f)
    )

    private val hidden = Props(
        alpha = Alpha(value = 0f)
    )

    override fun BackStackModel.State<InteractionTarget>.toProps(): List<MatchedProps<InteractionTarget, Props>> =
        listOf(
            MatchedProps(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedProps(it, hidden)
        }
}
