package com.bumble.appyx.interactions.core.ui.onscreen

fun interface OnScreenStateResolver<State> {

    fun isOnScreen(state: State): Boolean
}
