package com.github.zsoltk.composeribs.core

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

abstract class BackStackTransitionHandler : TransitionHandler<BackStack.TransitionState> {

    @Composable
    override fun handle(
        transitionState: BackStack.TransitionState,
        onRemovedFromScreen: () -> Unit,
        onDestroyed: () -> Unit,
    ): Modifier {
        val currentState = remember { MutableTransitionState(BackStack.TransitionState.default()) }
        currentState.targetState = transitionState
        val transition: Transition<BackStack.TransitionState> = updateTransition(currentState)

        when (transition.currentState) {
            BackStack.TransitionState.STASHED_IN_BACK_STACK -> onRemovedFromScreen()
            BackStack.TransitionState.DESTROYED -> onDestroyed()
            else -> {}
        }

        return map(transition)
    }

    @Composable
    abstract fun map(transition: Transition<BackStack.TransitionState>): Modifier
}
