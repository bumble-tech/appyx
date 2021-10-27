package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import com.github.zsoltk.composeribs.core.ChildTransitionScope
import com.github.zsoltk.composeribs.core.ChildTransitionScopeImpl

abstract class UpdateTransitionHandler<S>(open val isClipToBounds: Boolean = false) :
    TransitionHandler<S> {

    private fun Modifier.clipBounds(): Modifier =
        composed {
            if (isClipToBounds) {
                this.clipToBounds()
            } else {
                this
            }
        }

    @Composable
    private fun convertParamsToBounds(transitionParams: TransitionParams): IntSize {
        return if (isClipToBounds) {
            transitionParams.boundsDp
        } else {
            IntSize(
                LocalConfiguration.current.screenWidthDp,
                LocalConfiguration.current.screenHeightDp
            )
        }
    }

    @Composable
    override fun handle(
        transitionParams: TransitionParams,
        fromState: S,
        toState: S,
        onTransitionFinished: (S) -> Unit
    ): ChildTransitionScope<S> {
        val currentState = remember { MutableTransitionState(fromState) }
        currentState.targetState = toState
        val transition: Transition<S> = updateTransition(currentState)

        if (transition.currentState == currentState.targetState) {
            onTransitionFinished(currentState.targetState)
        }
        return ChildTransitionScopeImpl(
            transition = transition,
            transitionModifier = Modifier
                .clipBounds()
                .then(
                    map(
                        transition = transition,
                        transitionBoundsDp = convertParamsToBounds(transitionParams)
                    )
                )
        )
    }

    @Composable
    abstract fun map(transition: Transition<S>, transitionBoundsDp: IntSize): Modifier
}
