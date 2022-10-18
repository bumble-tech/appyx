package com.bumble.appyx.core.plugin

import androidx.annotation.WorkerThread
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.testing.ui.rules.AppyxTestActivity
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * [com.bumble.appyx.testing.ui.rules.AppyxActivityTestRule] based on [ActivityScenario] to support lifecycle tests.
 *
 * TODO: Consider merging with AppyxTestRule.
 */
class AppyxTestScenario<T : Node>(
    private val composeTestRule: ComposeTestRule = createEmptyComposeRule(),
    /** Add decorations like custom theme or CompositionLocalProvider. Do not forget to invoke `content()`. */
    private val decorator: (@Composable (content: @Composable () -> Unit) -> Unit) = { content -> content() },
    private val nodeFactory: NodeFactory<T>,
) : ComposeTestRule by composeTestRule {

    @get:WorkerThread
    val activityScenario: ActivityScenario<BackPressHandlerTestActivity> by lazy {
        val awaitNode = CountDownLatch(1)
        AppyxTestActivity.composableView = { activity ->
            decorator {
                NodeHost(integrationPoint = activity.appyxIntegrationPoint, factory = { buildContext ->
                    node = nodeFactory.create(buildContext)
                    awaitNode.countDown()
                    node
                })
            }
        }
        val scenario = ActivityScenario.launch(BackPressHandlerTestActivity::class.java)
        require(awaitNode.await(10, TimeUnit.SECONDS)) {
            "Timeout while waiting for node initialization"
        }
        waitForIdle()
        scenario
    }

    lateinit var node: T
        private set

    fun start() {
        activityScenario
    }

}
