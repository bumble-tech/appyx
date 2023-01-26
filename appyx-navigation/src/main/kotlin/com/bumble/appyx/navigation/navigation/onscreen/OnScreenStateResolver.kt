package com.bumble.appyx.navigation.navigation.onscreen

fun interface OnScreenStateResolver<State> {

    fun isOnScreen(state: State): Boolean
}
