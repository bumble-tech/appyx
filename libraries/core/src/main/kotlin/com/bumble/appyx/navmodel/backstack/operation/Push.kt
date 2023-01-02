package com.bumble.appyx.navmodel.backstack.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.State.CREATED
import com.bumble.appyx.navmodel.backstack.BackStack.State.STASHED
import com.bumble.appyx.navmodel.backstack.activeElement
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * [A, B, C] + Push(D) = [A, B, C, D]
 */
@Parcelize
data class Push<T : Parcelable>(
    private val element: T
) : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        element != elements.activeElement

    override fun invoke(elements: BackStackElements<T>): BackStackElements<T> =
        elements.transitionTo(STASHED) {
            it.targetState == ACTIVE
        } + BackStackElement(
            key = NavKey(element),
            fromState = CREATED,
            targetState = ACTIVE,
            operation = this
        )
}

fun <T : Parcelable> BackStack<T>.push(element: T) {
    accept(Push(element))
}
