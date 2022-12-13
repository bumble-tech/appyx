package com.bumble.appyx.app.node.backstack.app.custombackstack

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack.State
import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack.State.Created
import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack.State.Active
import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack.State.Stashed
import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack.State.Destroyed

object CustomBackStackOnScreenResolver : OnScreenStateResolver<State> {

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
