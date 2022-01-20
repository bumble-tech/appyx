package com.bumble.appyx.v2.core.routing.transition

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.bumble.appyx.v2.core.composable.ChildTransitionScope
import com.bumble.appyx.v2.core.composable.ChildTransitionScopeImpl

abstract class ModifierTransitionHandler<T, S>(open val clipToBounds: Boolean = false) :
    TransitionHandler<T, S> {

    private val clipToBoundsModifier: Modifier by lazy(LazyThreadSafetyMode.NONE) {
        if (clipToBounds) {
            Modifier.clipToBounds()
        } else {
            Modifier
        }
    }

    @Composable
    override fun handle(
        descriptor: TransitionDescriptor<T, S>,
        onTransitionFinished: (S) -> Unit
    ): ChildTransitionScope<S> {
        val transitionState = remember { MutableTransitionState(descriptor.fromState) }
        transitionState.targetState = descriptor.toState
        val transition: Transition<S> = updateTransition(transitionState)

        with(transition) {
            LaunchedEffect(currentState, targetState) {
                if (currentState == targetState) {
                    onTransitionFinished(targetState)
                }
            }
        }
        return rememberTransitionScope(transition, descriptor)
    }

    abstract fun createModifier(
        modifier: Modifier,
        transition: Transition<S>,
        descriptor: TransitionDescriptor<T, S>
    ): Modifier

    @Composable
    private fun rememberTransitionScope(
        transition: Transition<S>,
        descriptor: TransitionDescriptor<T, S>
    ) = remember(transition, descriptor) {
        ChildTransitionScopeImpl(
            transition = transition,
            transitionModifier = clipToBoundsModifier
                .then(
                    createModifier(
                        clipToBoundsModifier,
                        transition = transition,
                        descriptor = descriptor
                    )
                )
        )
    }
}
