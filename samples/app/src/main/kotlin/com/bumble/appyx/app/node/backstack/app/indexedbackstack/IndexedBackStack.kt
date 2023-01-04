package com.bumble.appyx.app.node.backstack.app.indexedbackstack

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.state.SavedStateMap

class IndexedBackStack<NavTarget : Any>(
    savedState: SavedStateMap?,
    initialElement: NavTarget
) : BaseNavModel<NavTarget, IndexedBackStack.State>(
    screenResolver = IndexedBackStackOnScreenResolver,
    finalStates = setOf(State.Destroyed),
    savedStateMap = savedState
) {

    sealed interface State {
        object Created : State
        object Active : State
        class Stashed(
            val index: Int,
            val size: Int
        ) : State

        object Destroyed : State
    }

    override val initialElements: NavElements<NavTarget, State> =
        listOf(
            IndexedBackStackElement(
                key = NavKey(initialElement),
                fromState = State.Active,
                targetState = State.Active,
                operation = Operation.Noop()
            )
        )
}
