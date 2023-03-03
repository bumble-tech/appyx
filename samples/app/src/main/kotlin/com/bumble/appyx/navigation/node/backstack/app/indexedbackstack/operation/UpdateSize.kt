package com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.operation

import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Destroyed
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Stashed
import com.bumble.appyx.core.navigation.NavElements
import kotlinx.parcelize.Parcelize

@Parcelize
class UpdateSize<T : Any> : IndexedBackStackOperation<T> {

    override fun isApplicable(elements: NavElements<T, IndexedBackStack.State>): Boolean = true

    override fun invoke(elements: NavElements<T, IndexedBackStack.State>): NavElements<T, IndexedBackStack.State> {
        val destroyedCount = elements.count { it.targetState is Destroyed }
        return elements.mapIndexed { index, element ->
            when (element.targetState) {
                is Stashed ->
                    element.transitionTo(
                        newTargetState = Stashed(
                            index = index,
                            size = elements.size - destroyedCount
                        ),
                        operation = this
                    )
                else -> element
            }
        }
    }
}
