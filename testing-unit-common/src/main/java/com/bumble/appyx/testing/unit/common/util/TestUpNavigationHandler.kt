package com.bumble.appyx.testing.unit.common.util

import com.bumble.appyx.core.routing.upnavigation.UpNavigationHandler
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestUpNavigationHandler : UpNavigationHandler {
    var invoked: Boolean = false

    override fun handleUpNavigation() {
        invoked = true
    }

    fun assertInvoked() {
        assertTrue(invoked, "FallbackUpNavigationHandler.handle was not invoked")
    }

    fun assertNotInvoked() {
        assertFalse(invoked, "FallbackUpNavigationHandler.handle was invoked")
    }
}
