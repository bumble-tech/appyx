package com.github.zsoltk.composeribs.core.plugin

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class StubUpNavigationHandler(
    var stub: Boolean = true
) : UpNavigationHandler {
    var invoked: Boolean = false

    override fun handleUpNavigation(): Boolean {
        invoked = true
        return stub
    }

    fun assertInvoked() {
        assertTrue("UpNavigationHandler.handleUpNavigation was not invoked", invoked)
    }

    fun assertNotInvoked() {
        assertFalse("UpNavigationHandler.handleUpNavigation was invoked", invoked)
    }

}
