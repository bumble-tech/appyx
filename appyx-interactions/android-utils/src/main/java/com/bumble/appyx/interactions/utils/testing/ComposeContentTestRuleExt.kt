package com.bumble.appyx.interactions.utils.testing

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.interactions.model.BaseAppyxComponent

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
