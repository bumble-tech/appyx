package com.bumble.appyx.appyxnavigation.node.backstack.app.indexedbackstack.operation

import com.bumble.appyx.appyxnavigation.node.backstack.app.indexedbackstack.IndexedBackStack
import com.bumble.appyx.appyxnavigation.node.backstack.app.indexedbackstack.IndexedBackStack.State
import com.bumble.appyx.appyxnavigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Active
import com.bumble.appyx.appyxnavigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Stashed
import com.bumble.appyx.appyxnavigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Destroyed
import com.bumble.appyx.core.navigation.NavElements
import kotlinx.parcelize.Parcelize

@Parcelize
class Pop<T : Any> : IndexedBackStackOperation<T> {

    override fun isApplicable(elements: NavElements<T, State>): Boolean =
        elements.any { it.targetState is Active } &&
                elements.filter { it.targetState !is Destroyed }.size > 1

    override fun invoke(elements: NavElements<T, State>): NavElements<T, State> {
        val destroyIndex = elements.indexOfLast { it.targetState is Active }
        val unStashIndex = elements.indexOfLast { it.targetState is Stashed }

        return elements
            .transitionToIndexed(Destroyed) { index, _ -> index == destroyIndex }
            .transitionToIndexed(Active) { index, _ -> index == unStashIndex }
    }
}

fun <T : Any> IndexedBackStack<T>.pop() {
    accept(Pop())
    accept(UpdateSize())
}
