package com.github.zsoltk.composeribs.core.routing

interface OnScreenStateResolver<State> {

    fun isOnScreen(state: State): Boolean
}
