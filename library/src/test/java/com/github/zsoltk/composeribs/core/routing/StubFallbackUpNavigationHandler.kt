package com.github.zsoltk.composeribs.core.routing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class StubFallbackUpNavigationHandler : FallbackUpNavigationHandler {
    var invoked: Boolean = false

    override fun handle() {
        invoked = true
    }

    fun assertInvoked() {
        assertTrue("FallbackUpNavigationHandler.handle was not invoked", invoked)
    }

    fun assertNotInvoked() {
        assertFalse("FallbackUpNavigationHandler.handle was invoked", invoked)
    }

}
