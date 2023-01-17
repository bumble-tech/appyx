package com.bumble.appyx.transitionmodel.backstack.operation

import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStack.State
import com.bumble.appyx.transitionmodel.backstack.activeIndex
import com.bumble.appyx.interactions.Parcelize

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
@Parcelize
class Pop<NavTarget : Any> : BackStackOperation<NavTarget> {

    override fun isApplicable(elements: NavElements<NavTarget, State>): Boolean =
        elements.any { it.state == State.ACTIVE } &&
            elements.any { it.state == State.STASHED }


    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        val destroyIndex = elements.activeIndex
        val unStashIndex =
            elements.indexOfLast { it.state == State.STASHED }
        require(destroyIndex != -1) { "Nothing to destroy, state=$elements" }
        require(unStashIndex != -1) { "Nothing to remove from stash, state=$elements" }
        val target = elements.mapIndexed { index, element ->
            when (index) {
                destroyIndex -> element.transitionTo(
                    newTargetState = State.POPPED,
                    operation = this
                )
                unStashIndex -> element.transitionTo(
                    newTargetState = State.ACTIVE,
                    operation = this
                )
                else -> element
            }
        }

        return NavTransition(
            fromState = elements,
            targetState = target
        )
    }

    override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

    override fun hashCode(): Int = this.javaClass.hashCode()
}

fun <NavTarget : Any> BackStack<NavTarget>.pop() {
    enqueue(Pop())
}
