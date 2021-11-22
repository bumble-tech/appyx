package com.github.zsoltk.composeribs.core.routing

class AlwaysOnScreenResolver<State> : OnScreenResolver<State> {

    override fun isOnScreen(state: State): Boolean = true
}
