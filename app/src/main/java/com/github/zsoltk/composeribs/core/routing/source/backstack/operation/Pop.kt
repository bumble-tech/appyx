package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.Operation
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.UuidGenerator

internal class Pop<T> : Operation<T> {

    override fun isApplicable(elements: List<BackStackElement<T>>): Boolean =
        elements.size > 1

    override fun invoke(
        elements: List<BackStackElement<T>>,
        uuidGenerator: UuidGenerator
    ): List<BackStackElement<T>> {

        val destroyIndex =
            elements.indexOfLast { it.targetState == BackStack.TransitionState.ON_SCREEN }
        val unStashIndex =
            elements.indexOfLast { it.targetState == BackStack.TransitionState.STASHED_IN_BACK_STACK }
        require(destroyIndex != -1) { "Nothing to destroy, state=$elements" }
        require(unStashIndex != -1) { "Nothing to remove from stash, state=$elements" }
        return elements.mapIndexed { index, element ->
            when (index) {
                destroyIndex -> element.copy(targetState = BackStack.TransitionState.DESTROYED)
                unStashIndex -> element.copy(targetState = BackStack.TransitionState.ON_SCREEN)
                else -> element
            }
        }
    }
}

fun <T> BackStack<T>.pop() {
    perform(Pop())
}
