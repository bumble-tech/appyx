package com.bumble.appyx.navmodel.backstack.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStack.State.ACTIVE
import com.bumble.appyx.navmodel.backstack.BackStack.State.CREATED
import com.bumble.appyx.navmodel.backstack.BackStack.State.DESTROYED
import com.bumble.appyx.navmodel.backstack.BackStackElement
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.activeElement
import com.bumble.appyx.navmodel.backstack.activeIndex
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * [A, B, C] + Replace(D) = [A, B, D]
 */
@Parcelize
data class Replace<T : Parcelable>(
    private val element: T
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

fun <T : Parcelable> BackStack<T>.replace(element: T) {
    accept(Replace(element))
}
