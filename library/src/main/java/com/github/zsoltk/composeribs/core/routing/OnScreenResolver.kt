package com.github.zsoltk.composeribs.core.routing

interface OnScreenResolver<State> {

    fun isOnScreen(state: State): Boolean
}
