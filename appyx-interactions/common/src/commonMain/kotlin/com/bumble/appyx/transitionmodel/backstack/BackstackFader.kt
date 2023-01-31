package com.bumble.appyx.transitionmodel.backstack

import androidx.compose.animation.core.spring
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

class BackstackFader<NavTarget : Any>() :
    BaseInterpolator<NavTarget, BackStackModel.State<NavTarget>, BackstackFader.Props>({
        Props(alpha = Alpha(0f))
    }) {

    class Props(
        var alpha: Alpha,
        override val isVisible: Boolean = false
    ) : Interpolatable<Props>, Animatable<Props>, HasModifier, BaseProps {

        override suspend fun lerpTo(start: Props, end: Props, fraction: Float) {
            alpha.lerpTo(start.alpha, end.alpha, fraction)
        }

        override val modifier: Modifier
            get() = Modifier
                .then(alpha.modifier)

        suspend fun animateTo(scope: CoroutineScope, props: Props) {
            scope.launch {
                alpha.animateTo(props.alpha.value, spring())
            }
        }

        override suspend fun animateTo(
            scope: CoroutineScope,
            props: Props,
            onStart: () -> Unit,
            onFinished: () -> Unit
        ) {
            scope.launch {
                onStart()
                alpha.animateTo(props.alpha.value, spring())
                onFinished()
            }
        }
    }

    private val visible = Props(
        alpha = Alpha(1f),
        isVisible = true
    )

    private val hidden = Props(
        alpha = Alpha(0f),
        isVisible = false
    )

    override fun BackStackModel.State<NavTarget>.toProps(): List<MatchedProps<NavTarget, Props>> =
        listOf(
            MatchedProps(active, visible)
        ) + (created + stashed + destroyed).map {
            MatchedProps(it, hidden)
        }
}