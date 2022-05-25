package com.bumble.appyx.v2.core.routing.source.backstack.operation

import com.bumble.appyx.v2.core.routing.RoutingKey
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElement
import com.bumble.appyx.v2.core.routing.source.backstack.BackStackElements
import com.bumble.appyx.v2.core.routing.source.backstack.activeIndex
import com.bumble.appyx.v2.core.routing.source.backstack.activeRouting
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
        element != elements.activeRouting

    override fun invoke(
        elements: BackStackElements<T>
    ): BackStackElements<T> {
        require(elements.any { it.targetState == BackStack.TransitionState.ACTIVE }) { "No element to be replaced, state=$elements" }

        return elements.mapIndexed { index, element ->
            if (index == elements.activeIndex) {
                element.transitionTo(
                    targetState = BackStack.TransitionState.DESTROYED,
                    operation = this
                )
            } else {
                element
            }
        } + BackStackElement(
            key = RoutingKey(element),
            fromState = BackStack.TransitionState.CREATED,
            targetState = BackStack.TransitionState.ACTIVE,
            operation = this,
        )
    }
}

fun <T : Any> BackStack<T>.replace(element: T) {
    accept(Replace(element))
}
