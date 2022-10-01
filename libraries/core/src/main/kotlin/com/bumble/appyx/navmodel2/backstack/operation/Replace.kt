package com.bumble.appyx.navmodel2.backstack.operation

import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel2.backstack.BackStack
import com.bumble.appyx.navmodel2.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.navmodel2.backstack.BackStack.State.CREATED
import com.bumble.appyx.navmodel2.backstack.BackStack.State.DESTROYED
import com.bumble.appyx.navmodel2.backstack.BackStackElement
import com.bumble.appyx.navmodel2.backstack.BackStackElements
import com.bumble.appyx.navmodel2.backstack.activeElement
import com.bumble.appyx.navmodel2.backstack.activeIndex
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * [A, B, C] + Replace(D) = [A, B, D]
 */
@Parcelize
data class Replace<T : Any>(
    private val element: @RawValue T
) : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        element != elements.activeElement

    override fun invoke(
        elements: BackStackElements<T>
    ): BackStackElements<T> {
        require(elements.any { it.targetState == ACTIVE }) { "No element to be replaced, state=$elements" }

        return elements.transitionToIndexed(DESTROYED) { index, _ ->
            index == elements.activeIndex
        } + BackStackElement(
            key = NavKey(element),
            fromState = CREATED,
            targetState = ACTIVE,
            operation = this,
        )
    }
}

fun <T : Any> BackStack<T>.replace(element: T) {
    enqueue(Replace(element))
}
