package com.bumble.appyx.core.navigation.onscreen

interface OnScreenStateResolver<State> {

    fun isOnScreen(state: State): Boolean
}
