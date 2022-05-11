package com.bumble.appyx.v2.core.routing.onscreen

class AlwaysOnScreenResolver<State> : OnScreenStateResolver<State> {

    override fun isOnScreen(state: State): Boolean = true
}
