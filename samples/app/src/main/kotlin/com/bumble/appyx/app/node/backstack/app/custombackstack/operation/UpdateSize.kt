package com.bumble.appyx.app.node.backstack.app.custombackstack.operation

import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack
import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack.State.Destroyed
import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack.State.Stashed
import com.bumble.appyx.core.navigation.NavElements
import kotlinx.parcelize.Parcelize

@Parcelize
class UpdateSize<T : Any> : CustomBackStackOperation<T> {

    override fun isApplicable(elements: NavElements<T, CustomBackStack.State>): Boolean = true

    override fun invoke(elements: NavElements<T, CustomBackStack.State>): NavElements<T, CustomBackStack.State> {
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
