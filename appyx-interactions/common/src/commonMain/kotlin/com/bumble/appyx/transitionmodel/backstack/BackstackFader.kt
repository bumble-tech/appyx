package com.bumble.appyx.transitionmodel.backstack

import DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.UiContext
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.transitionmodel.BaseInterpolator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BackstackFader<NavTarget : Any>(
    uiContext: UiContext,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseInterpolator<NavTarget, BackStackModel.State<NavTarget>, BackstackFader.Props>(
    scope = uiContext.coroutineScope,
    defaultAnimationSpec = defaultAnimationSpec,
) {
    override fun defaultProps(): Props = Props()

    class Props(
        var alpha: Alpha = Alpha(1f),
    ) : BaseProps(listOf(alpha.isAnimating)), Animatable<Props>, HasModifier {

        override fun isVisible() =
            alpha.value > 0.0f

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
            scope.launch {
                alpha.animateTo(props.alpha.value, springSpec) {
                    updateVisibilityState()
                }
            }
        }

        override fun lerpTo(scope: CoroutineScope, start: Props, end: Props, fraction: Float) {
            scope.launch {
                alpha.lerpTo(start.alpha, end.alpha, fraction)
                updateVisibilityState()
            }
        }
    }

    private val visible = Props(
        alpha = Alpha(1f)
    )

    private val hidden = Props(
        alpha = Alpha(0f)
    )

    override fun BackStackModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        listOf(
            MatchedProps(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedProps(it, hidden)
        }
}
