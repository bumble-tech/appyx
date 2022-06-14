package com.bumble.appyx.testing.ui.rules

import androidx.annotation.CallSuper
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.bumble.appyx.v2.core.node.NodeView
import com.bumble.appyx.v2.core.node.ViewFactory
import org.junit.runner.Description
import org.junit.runners.model.Statement

open class AppyxViewTestRule<View : NodeView>(
    viewFactory: ViewFactory<View>,
    private val composeTestRule: ComposeTestRule = createAndroidComposeRule<AppyxViewActivity>()
) : ComposeTestRule by composeTestRule {

    val view = viewFactory.invoke()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    before()
                    composeTestRule.apply(base, description)
                } finally {
                    after()
                }
            }
        }
    }

    @CallSuper
    open fun before() {
        AppyxViewActivity.view = view
    }

    @CallSuper
    open fun after() {
        AppyxViewActivity.view = null
    }

}
