package com.bumble.appyx.v2.core.routing

class AlwaysOnScreenResolver<State> : OnScreenStateResolver<State> {

    override fun isOnScreen(state: State): Boolean = true
}
