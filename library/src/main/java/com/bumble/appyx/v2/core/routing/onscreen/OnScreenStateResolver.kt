package com.bumble.appyx.v2.core.routing.onscreen

interface OnScreenStateResolver<State> {

    fun isOnScreen(state: State): Boolean
}
