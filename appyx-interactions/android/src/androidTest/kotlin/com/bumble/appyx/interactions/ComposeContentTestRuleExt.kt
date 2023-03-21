package com.bumble.appyx.interactions

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.interactions.core.model.BaseInteractionModel

fun BaseInteractionModel<*, *>.waitUntilAnimationStarted(rule: ComposeContentTestRule) {
    rule.mainClock.advanceTimeUntil {
        isAnimating.value
    }
}

fun BaseInteractionModel<*, *>.waitUntilAnimationEnded(rule: ComposeContentTestRule) {
    rule.mainClock.advanceTimeUntil {
        !isAnimating.value
    }
}
