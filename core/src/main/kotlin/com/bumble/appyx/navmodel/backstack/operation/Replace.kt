package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.RoutingKey
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.activeIndex
import com.bumble.appyx.navmodel.backstack.activeRouting
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.CREATED
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
        require(elements.any { it.targetState == ACTIVE }) { "No element to be replaced, state=$elements" }

        return elements.mapIndexed { index, element ->
            if (index == elements.activeIndex) {
                element.transitionTo(
                    newTargetState = BackStack.TransitionState.DESTROYED,
                    operation = this
                )
            } else {
                element
            }
        } + BackStackElement(
            key = RoutingKey(element),
            fromState = CREATED,
            targetState = ACTIVE,
            operation = this,
        )
    }
}

fun <T : Any> BackStack<T>.replace(element: T) {
    accept(Replace(element))
}
