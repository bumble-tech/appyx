package com.bumble.appyx.core.navigation.onscreen

fun interface OnScreenStateResolver<State> {

    fun isOnScreen(state: State): Boolean
}
