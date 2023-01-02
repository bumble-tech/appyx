package com.bumble.appyx.app.node.backstack.app.indexedbackstack

import android.os.Parcelable
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.state.SavedStateMap
import kotlinx.parcelize.Parcelize

class IndexedBackStack<NavTarget: Parcelable>(
    savedState: SavedStateMap?,
    initialElement: NavTarget
) : BaseNavModel<NavTarget, IndexedBackStack.State>(
    screenResolver = IndexedBackStackOnScreenResolver,
    finalStates = setOf(State.Destroyed),
    savedStateMap = savedState
) {

    sealed interface State : Parcelable {
        @Parcelize
        object Created : State

        @Parcelize
        object Active : State

        @Parcelize
        class Stashed(
            val index: Int,
            val size: Int
        ) : State

        @Parcelize
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
