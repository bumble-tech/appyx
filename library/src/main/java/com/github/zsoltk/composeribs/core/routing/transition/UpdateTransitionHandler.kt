package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.github.zsoltk.composeribs.core.ChildTransitionScope
import com.github.zsoltk.composeribs.core.ChildTransitionScopeImpl

abstract class UpdateTransitionHandler<S>(open val clipToBounds: Boolean = false) :
    TransitionHandler<S> {

    private val clipToBoundsModifier: Modifier by lazy(LazyThreadSafetyMode.NONE) {
        if (clipToBounds) {
            Modifier.clipToBounds()
        } else {
            Modifier
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
        val transitionBounds = convertParamsToBounds(transitionParams)
        return rememberTransitionScope(transition, transitionBounds)
    }

    abstract fun map(
        modifier: Modifier,
        transition: Transition<S>,
        transitionBounds: TransitionBounds
    ): Modifier

    @Composable
    private fun convertParamsToBounds(transitionParams: TransitionParams): TransitionBounds {
        return if (clipToBounds) {
            transitionParams.bounds
        } else {
            with(LocalDensity.current) {
                val configuration = LocalConfiguration.current
                TransitionBounds(
                    width = configuration.screenWidthDp.toDp(),
                    height = configuration.screenHeightDp.toDp()
                )
            }
        }
    }

    @Composable
    private fun rememberTransitionScope(
        transition: Transition<S>,
        transitionBounds: TransitionBounds
    ) = remember(transition, transitionBounds) {
        ChildTransitionScopeImpl(
            transition = transition,
            transitionModifier = clipToBoundsModifier
                .then(
                    map(
                        clipToBoundsModifier,
                        transition = transition,
                        transitionBounds = transitionBounds
                    )
                )
        )
    }
}
