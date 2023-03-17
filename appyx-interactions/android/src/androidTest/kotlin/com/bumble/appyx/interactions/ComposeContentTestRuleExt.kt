package com.bumble.appyx.interactions

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.interactions.core.InteractionModel

fun InteractionModel<*, *>.waitUntilAnimationStarted(rule: ComposeContentTestRule) {
    rule.mainClock.advanceTimeUntil {
        isAnimating.value
    }
}

fun InteractionModel<*, *>.waitUntilAnimationEnded(rule: ComposeContentTestRule) {
    rule.mainClock.advanceTimeUntil {
        !isAnimating.value
    }
}
