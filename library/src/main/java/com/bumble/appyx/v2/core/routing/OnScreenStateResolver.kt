package com.bumble.appyx.v2.core.routing

interface OnScreenStateResolver<State> {

    fun isOnScreen(state: State): Boolean
}
