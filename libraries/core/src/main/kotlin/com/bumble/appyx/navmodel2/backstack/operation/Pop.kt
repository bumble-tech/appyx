package com.bumble.appyx.navmodel2.backstack.operation

import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.core.navigation2.NavTransition
import com.bumble.appyx.navmodel2.backstack.BackStack
import com.bumble.appyx.navmodel2.backstack.BackStackElements
import com.bumble.appyx.navmodel2.backstack.activeIndex
import kotlinx.parcelize.Parcelize

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
@Parcelize
class Pop<NavTarget : Any> : BackStackOperation<NavTarget> {

    override fun isApplicable(elements: BackStackElements<NavTarget>): Boolean =
        elements.any { it.state == BackStack.State.ACTIVE } &&
            elements.any { it.state == BackStack.State.STASHED }


    override fun invoke(elements: NavElements<NavTarget, BackStack.State>): NavTransition<NavTarget, BackStack.State> {
        val destroyIndex = elements.activeIndex
        val unStashIndex =
            elements.indexOfLast { it.state == BackStack.State.STASHED }
        require(destroyIndex != -1) { "Nothing to destroy, state=$elements" }
        require(unStashIndex != -1) { "Nothing to remove from stash, state=$elements" }
        val target = elements.mapIndexed { index, element ->
            when (index) {
                destroyIndex -> element.transitionTo(
                    newTargetState = BackStack.State.POPPED,
                    operation = this
                )
                unStashIndex -> element.transitionTo(
                    newTargetState = BackStack.State.ACTIVE,
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
