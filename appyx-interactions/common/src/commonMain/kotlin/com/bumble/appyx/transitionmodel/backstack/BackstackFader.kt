package com.bumble.appyx.transitionmodel.backstack

import DefaultAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.transitionmodel.BaseInterpolator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BackstackFader<NavTarget : Any>(
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec
) : BaseInterpolator<NavTarget, BackStackModel.State<NavTarget>, BackstackFader.Props>(
    defaultAnimationSpec = defaultAnimationSpec
) {
    override fun defaultProps(): Props = Props()

    class Props(
        var alpha: Alpha = Alpha(1f),
    ) : Interpolatable<Props>, Animatable<Props>, HasModifier, BaseProps {

        override val isVisible: Boolean
            get() = alpha.value > 0.0f

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            alpha.lerpTo(start.alpha, end.alpha, fraction)
        }

        override val modifier: Modifier
            get() = Modifier
                .then(alpha.modifier)

        override suspend fun snapTo(scope: CoroutineScope, props: Props) {
            alpha.snapTo(props.alpha.value)
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: Props,
            springSpec: SpringSpec<Float>,
            onStart: () -> Unit,
            onFinished: () -> Unit
        ) {
            scope.launch {
                onStart()
                alpha.animateTo(props.alpha.value, springSpec)
                onFinished()
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
