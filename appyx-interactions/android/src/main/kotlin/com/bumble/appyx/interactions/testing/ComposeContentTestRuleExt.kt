package com.bumble.appyx.interactions.testing

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent

fun BaseAppyxComponent<*, *>.waitUntilAnimationStarted(rule: ComposeContentTestRule) {
    rule.mainClock.advanceTimeUntil {
        isAnimating.value
    }
}

fun BaseAppyxComponent<*, *>.waitUntilAnimationEnded(rule: ComposeContentTestRule) {
    rule.mainClock.advanceTimeUntil {
        !isAnimating.value
    }
}
