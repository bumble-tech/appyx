package com.bumble.appyx.testing.ui.rules

import androidx.annotation.CallSuper
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.rule.ActivityTestRule
import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.node.Node
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

open class AppyxTestRule<T : Node>(
    private val launchActivity: Boolean = true,
    private val composeTestRule: ComposeTestRule = createEmptyComposeRule(),
    /** Add decorations like custom theme or CompositionLocalProvider. Do not forget to invoke `content()`. */
    @Suppress("unused")
    private val decorator: (@Composable (content: @Composable () -> Unit) -> Unit) = { content -> content() },
    private val nodeFactory: NodeFactory<T>,
) : ActivityTestRule<AppyxViewActivity>(
    /* activityClass = */ AppyxViewActivity::class.java,
    /* initialTouchMode = */ true,
    /* launchActivity = */ launchActivity
), ComposeTestRule by composeTestRule {

    lateinit var node: T
        private set

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
        AppyxViewActivity.composableView = { activity ->
            NodeHost(integrationPoint = activity.integrationPoint, factory = { buildContext ->
                node = nodeFactory.create(buildContext)
                node
            })
        }
    }

    override fun afterActivityLaunched() {
        AppyxViewActivity.composableView = null
    }

    @CallSuper
    open fun before() {

    }

    @CallSuper
    open fun after() {

    }

}
