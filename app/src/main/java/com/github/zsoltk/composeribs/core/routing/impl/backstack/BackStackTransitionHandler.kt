package com.github.zsoltk.composeribs.core.routing.impl.backstack

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.TransitionHandler

abstract class BackStackTransitionHandler : TransitionHandler<BackStack.TransitionState> {

    @Composable
    override fun handle(
        fromState: BackStack.TransitionState,
        toState: BackStack.TransitionState,
        onTransitionFinished: (BackStack.TransitionState) -> Unit,
    ): Modifier {
        val currentState = remember { MutableTransitionState(fromState) }
        currentState.targetState = toState
        val transition: Transition<BackStack.TransitionState> = updateTransition(currentState)

        if (transition.currentState == currentState.targetState) {
            onTransitionFinished(currentState.targetState)
        }

        return map(transition)
    }

    @Composable
    abstract fun map(transition: Transition<BackStack.TransitionState>): Modifier
}
