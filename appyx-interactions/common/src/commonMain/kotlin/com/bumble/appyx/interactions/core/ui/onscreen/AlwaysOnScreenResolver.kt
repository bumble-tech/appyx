package com.bumble.appyx.interactions.core.ui.onscreen

class AlwaysOnScreenResolver<State> : OnScreenStateResolver<State> {

    override fun isOnScreen(state: State): Boolean = true
}
