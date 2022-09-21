package com.bumble.appyx.navmodel.backstack.operation

import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState
import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.*
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.activeElement
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * [A, B, C] + Push(D) = [A, B, C, D]
 */
@Parcelize
data class Push<T : Any>(
    private val element: @RawValue T
) : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        element != elements.activeElement

    override fun invoke(elements: BackStackElements<T>): BackStackElements<T> =
        elements.transitionTo(STASHED_IN_BACK_STACK) { element ->
            element.targetState == ACTIVE
        } + BackStackElement(
            key = NavKey(element),
            fromState = CREATED,
            targetState = ACTIVE,
            operation = this
        )
}

fun <T : Any> BackStack<T>.push(element: T) {
    accept(Push(element))
}
