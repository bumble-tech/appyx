package com.bumble.appyx.app.node.backstack.app.indexedbackstack.operation

import com.bumble.appyx.app.node.backstack.app.indexedbackstack.IndexedBackStack
import com.bumble.appyx.app.node.backstack.app.indexedbackstack.IndexedBackStack.State.Created
import com.bumble.appyx.app.node.backstack.app.indexedbackstack.IndexedBackStack.State.Active
import com.bumble.appyx.app.node.backstack.app.indexedbackstack.IndexedBackStack.State.Stashed
import com.bumble.appyx.app.node.backstack.app.indexedbackstack.IndexedBackStackElement
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class Push<T : Any>(
    private val element: @RawValue T
) : IndexedBackStackOperation<T> {

    override fun isApplicable(elements: NavElements<T, IndexedBackStack.State>): Boolean =
        elements.any { it.targetState is Active }

    override fun invoke(elements: NavElements<T, IndexedBackStack.State>): NavElements<T, IndexedBackStack.State> {
        val active = elements.findLast { it.targetState is Active }
        val stashed = Stashed(
            index = elements.indexOf(active),
            size = elements.size
        )
        val convertToStashed = elements.transitionTo(stashed) { it == active }
        val newElement = IndexedBackStackElement(
            key = NavKey(element),
            fromState = Created,
            targetState = Active,
            operation = this
        )

        return convertToStashed + newElement
    }
}

fun <T : Any> IndexedBackStack<T>.push(element: T) {
    accept(Push(element))
    accept(UpdateSize())
}
