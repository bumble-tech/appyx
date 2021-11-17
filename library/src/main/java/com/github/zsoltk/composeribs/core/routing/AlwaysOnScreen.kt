package com.github.zsoltk.composeribs.core.routing

import kotlinx.parcelize.Parcelize

@Parcelize
class AlwaysOnScreen<State> : OnScreenResolver<State> {
    override fun isOnScreen(state: State): Boolean = true
}
