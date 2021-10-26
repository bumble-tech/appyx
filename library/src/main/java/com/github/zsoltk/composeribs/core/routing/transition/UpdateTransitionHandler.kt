package com.github.zsoltk.composeribs.core.routing.transition

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.ChildTransitionScope
import com.github.zsoltk.composeribs.core.ChildTransitionScopeImpl

abstract class UpdateTransitionHandler<S> : TransitionHandler<S> {

    @Composable
    override fun handle(
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
        return ChildTransitionScopeImpl(transition, map(transition))
    }

    @Composable
    abstract fun map(transition: Transition<S>): Modifier
}
