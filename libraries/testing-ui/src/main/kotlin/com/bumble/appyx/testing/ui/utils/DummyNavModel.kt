package com.bumble.appyx.testing.ui.utils

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver

class DummyNavModel<NavTarget : Any, State> : BaseNavModel<NavTarget, State>(
    savedStateMap = null,
    finalState = null,
    screenResolver = OnScreenStateResolver { true }
) {
    override val initialElements: NavElements<NavTarget, State>
        get() = emptyList()

}
