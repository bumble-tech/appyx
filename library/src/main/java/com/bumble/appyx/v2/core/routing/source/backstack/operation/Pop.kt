package com.bumble.appyx.v2.core.routing.source.backstack.operation

import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElements
import com.bumble.appyx.v2.core.routing.source.backstack.currentIndex
import kotlinx.parcelize.Parcelize

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
@Parcelize
class Pop<T : Any> : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        elements.any { it.targetState == BackStack.TransitionState.ACTIVE } &&
                elements.any { it.targetState == BackStack.TransitionState.STASHED_IN_BACK_STACK }

    override fun invoke(
        elements: BackStackElements<T>
    ): BackStackElements<T> {

        val destroyIndex = elements.currentIndex
        val unStashIndex =
            elements.indexOfLast { it.targetState == BackStack.TransitionState.STASHED_IN_BACK_STACK }
        require(destroyIndex != -1) { "Nothing to destroy, state=$elements" }
        require(unStashIndex != -1) { "Nothing to remove from stash, state=$elements" }
        return elements.mapIndexed { index, element ->
            when (index) {
                destroyIndex -> element.transitionTo(
                    targetState = BackStack.TransitionState.DESTROYED,
                    operation = this
                )
                unStashIndex -> element.transitionTo(
                    targetState = BackStack.TransitionState.ACTIVE,
                    operation = this
                )
                else -> element
            }
        }
    }

    override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

    override fun hashCode(): Int = this.javaClass.hashCode()
}

fun <T : Any> BackStack<T>.pop() {
    accept(Pop())
}
