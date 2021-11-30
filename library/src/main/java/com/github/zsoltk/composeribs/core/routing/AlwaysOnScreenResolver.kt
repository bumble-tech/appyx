package com.github.zsoltk.composeribs.core.routing

class AlwaysOnScreenResolver<State> : OnScreenStateResolver<State> {

    override fun isOnScreen(state: State): Boolean = true
}
