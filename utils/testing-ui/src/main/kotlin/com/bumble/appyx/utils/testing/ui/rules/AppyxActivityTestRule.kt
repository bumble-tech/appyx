package com.bumble.appyx.utils.testing.ui.rules

import androidx.annotation.CallSuper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.rule.ActivityTestRule
import com.bumble.appyx.navigation.integration.NodeFactory
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

open class AppyxActivityTestRule<T : Node<*>>(
    private val launchActivity: Boolean = true,
    private val composeTestRule: ComposeTestRule = createEmptyComposeRule(),
    /** Add decorations like custom theme or CompositionLocalProvider. Do not forget to invoke `content()`. */
    private val decorator: (@Composable (content: @Composable () -> Unit) -> Unit) = { content -> content() },
    private val nodeFactory: NodeFactory<T>,
) : ActivityTestRule<AppyxTestActivity>(
    /* activityClass = */ AppyxTestActivity::class.java,
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
        AppyxTestActivity.composableView = { activity ->
            decorator {
                NodeHost(
                    lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                    integrationPoint = activity.appyxV2IntegrationPoint,
                ) { nodeContext ->
                    node = nodeFactory.create(nodeContext)
                    node
                }
            }
        }
    }

    override fun afterActivityLaunched() {
        AppyxTestActivity.composableView = null
    }

    @CallSuper
    open fun before() {

    }

    @CallSuper
    open fun after() {

    }

}
