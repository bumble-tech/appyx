package com.bumble.appyx.core.routing.upnavigation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class TestUpNavigationHandler : UpNavigationHandler {
    var invoked: Boolean = false

    override fun handleUpNavigation() {
        invoked = true
    }

    fun assertInvoked() {
        assertTrue("FallbackUpNavigationHandler.handle was not invoked", invoked)
    }

    fun assertNotInvoked() {
        assertFalse("FallbackUpNavigationHandler.handle was invoked", invoked)
    }

}
