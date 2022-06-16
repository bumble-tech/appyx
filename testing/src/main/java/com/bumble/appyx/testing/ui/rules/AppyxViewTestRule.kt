package com.bumble.appyx.testing.ui.rules

import androidx.annotation.CallSuper
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.rule.ActivityTestRule
import com.bumble.appyx.core.node.NodeView
import com.bumble.appyx.core.node.ViewFactory
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

open class AppyxViewTestRule<View : NodeView>(
    viewFactory: ViewFactory<View>,
    private val launchActivity: Boolean = true,
    private val composeTestRule: ComposeTestRule = createEmptyComposeRule()
) : ActivityTestRule<AppyxViewActivity>(
    /* activityClass = */ AppyxViewActivity::class.java,
    /* initialTouchMode = */ true,
    /* launchActivity = */ launchActivity
), ComposeTestRule by composeTestRule {

    val view by lazy { viewFactory.invoke() }

    override fun apply(base: Statement, description: Description): Statement {
        val parent =
            TestRule { parentBase, parentDescription -> super.apply(parentBase, parentDescription) }

        return RunRules(base, listOf(composeTestRule, parent), description, ::before, ::after)
    }

    fun start() {
        require(!launchActivity) {
            "Activity will be launched automatically, launchActivity parameter was passed into constructor"
        }
        launchActivity(null)
    }

    override fun beforeActivityLaunched() {
        AppyxViewActivity.view = view
    }

    override fun afterActivityLaunched() {
        AppyxViewActivity.view = null
    }

    @CallSuper
    open fun before() {

    }

    @CallSuper
    open fun after() {

    }

}
