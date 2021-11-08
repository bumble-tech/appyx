package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.UuidGenerator
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElements
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackOperation
import com.github.zsoltk.composeribs.core.routing.source.backstack.currentIndex

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
class Pop<T : Any> : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        elements.any { it.targetState == BackStack.TransitionState.ON_SCREEN } &&
                elements.any { it.targetState == BackStack.TransitionState.STASHED_IN_BACK_STACK }

    override fun invoke(
        elements: BackStackElements<T>,
        uuidGenerator: UuidGenerator
    ): BackStackElements<T> {

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
