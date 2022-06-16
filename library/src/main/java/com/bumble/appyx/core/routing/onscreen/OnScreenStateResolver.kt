package com.bumble.appyx.core.routing.onscreen

interface OnScreenStateResolver<State> {

    fun isOnScreen(state: State): Boolean
}
