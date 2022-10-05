package com.bumble.appyx.testing.unit.common.util

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver

class DummyNavModel<NavTarget : Any, State> : BaseNavModel<NavTarget, State>(
    savedStateMap = null,
    finalState = null,
    screenResolver = object : OnScreenStateResolver<State> {
        override fun isOnScreen(state: State) = true
    },
    initialElements = emptyList()
)
