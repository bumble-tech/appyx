package com.bumble.appyx.interactions.core.ui.onscreen

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver

class AlwaysOnScreenResolver<State> : OnScreenStateResolver<State> {

    override fun isOnScreen(state: State): Boolean = true
}
