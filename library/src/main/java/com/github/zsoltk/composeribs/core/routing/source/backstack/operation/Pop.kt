package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.Operation
import com.github.zsoltk.composeribs.core.routing.source.backstack.Elements
import com.github.zsoltk.composeribs.core.routing.source.backstack.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.backstack.currentIndex

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
internal class Pop<T : Any> : Operation<T> {

    override fun isApplicable(elements: Elements<T>): Boolean =
        elements.isNotEmpty()

    override fun invoke(
        elements: Elements<T>,
        uuidGenerator: UuidGenerator
    ): Elements<T> {

        val destroyIndex = elements.currentIndex
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

fun <T : Any> BackStack<T>.pop() {
    perform(Pop())
}
