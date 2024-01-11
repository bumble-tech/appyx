package com.bumble.appyx.utils.testing.unit.common.util

import com.bumble.appyx.navigation.integration.UpNavigationHandler
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestUpNavigationHandler : UpNavigationHandler {
    var invoked: Boolean = false

    override fun handleUpNavigation() {
        invoked = true
    }

    fun assertInvoked() {
        assertTrue(invoked, "Has not navigated up")
    }

    fun assertNotInvoked() {
        assertFalse(invoked, "Has navigated up")
    }
}
