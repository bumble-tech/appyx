package com.bumble.appyx.navigation.node.backstack.app.indexedbackstack

import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Active
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Created
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Destroyed
import com.bumble.appyx.navigation.node.backstack.app.indexedbackstack.IndexedBackStack.State.Stashed
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver

object IndexedBackStackOnScreenResolver : OnScreenStateResolver<State> {

    override fun isOnScreen(state: State): Boolean =
        when (state) {
            is Created,
            is Active -> true
            is Stashed -> {
                if (state.size > MAX_ON_SCREEN) {
                    state.index in state.size - MAX_ON_SCREEN..state.size - 2
                } else {
                    true
                }
            }
            is Destroyed -> false
        }

    const val MAX_ON_SCREEN = 3
}
