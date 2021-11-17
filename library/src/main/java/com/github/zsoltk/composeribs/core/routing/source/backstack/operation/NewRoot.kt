package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.OnScreenResolver
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElements
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackOperation
import com.github.zsoltk.composeribs.core.routing.source.backstack.current

/**
 * Operation:
 *
 * [A, B, C] + NewRoot(D) = [ D ]
 */
data class NewRoot<T : Any>(
    private val onScreenResolver: OnScreenResolver<BackStack.TransitionState>,
    private val element: T
) : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean = true

    override fun invoke(
        elements: BackStackElements<T>,
    ): BackStackElements<T> {

        val current = elements.current
        requireNotNull(current) { "No previous elements, state=$elements" }

        return if (current.key.routing == element) {
            listOf(current)
        } else {
            listOf(
                current.transitionTo(
                    targetState = BackStack.TransitionState.DESTROYED,
                    operation = this
                ),
                BackStackElement(
                    onScreenResolver = onScreenResolver,
                    key = RoutingKey(element),
                    fromState = BackStack.TransitionState.CREATED,
                    targetState = BackStack.TransitionState.ON_SCREEN,
                    onScreen = true,
                    operation = this
                )
            )
        }
    }
}

fun <T : Any> BackStack<T>.newRoot(element: T) {
    perform(NewRoot(onScreenResolver, element))
}
