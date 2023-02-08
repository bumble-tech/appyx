package com.bumble.appyx.transitionmodel.backstack.interpolator

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.BaseProps
import com.bumble.appyx.interactions.core.ui.MatchedProps
import com.bumble.appyx.interactions.core.ui.property.Animatable
import com.bumble.appyx.interactions.core.ui.property.HasModifier
import com.bumble.appyx.interactions.core.ui.property.Interpolatable
import com.bumble.appyx.interactions.core.ui.property.impl.Alpha
import com.bumble.appyx.transitionmodel.BaseInterpolator
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

class BackStackCrossfader<NavTarget : Any> :
    BaseInterpolator<NavTarget, BackStackModel.State<NavTarget>, BackStackCrossfader.Props>() {

    override fun defaultProps(): Props = Props()

    class Props(
        val alpha: Alpha = Alpha(value = 1f),
    ) : Interpolatable<Props>, HasModifier, BaseProps, Animatable<Props> {

        override val isVisible: Boolean
            get() = alpha.value > 0.0f

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
            onStart()
            val a1 = scope.async {
                alpha.animateTo(
                    props.alpha.value,
                    spring(springSpec.dampingRatio, springSpec.stiffness)
                )
            }
            a1.await()
            onFinished()
        }

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            alpha.lerpTo(start.alpha, end.alpha, fraction)
        }

    }

    private val visible = Props(
        alpha = Alpha(value = 1f)
    )

    private val hidden = Props(
        alpha = Alpha(value = 0f)
    )

    override fun BackStackModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        listOf(
            MatchedProps(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedProps(it, hidden)
        }
}
