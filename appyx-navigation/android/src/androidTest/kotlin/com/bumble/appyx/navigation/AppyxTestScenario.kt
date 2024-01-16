package com.bumble.appyx.navigation

import androidx.annotation.WorkerThread
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import com.bumble.appyx.navigation.integration.NodeFactory
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import com.bumble.appyx.utils.testing.ui.rules.AppyxTestActivity
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AppyxTestScenario<T : Node>(
    private val composeTestRule: ComposeTestRule = createEmptyComposeRule(),
    /** Add decorations like custom theme or CompositionLocalProvider. Do not forget to invoke `content()`. */
    private val decorator: (@Composable (content: @Composable () -> Unit) -> Unit) = { content -> content() },
    private val nodeFactory: NodeFactory<T>,
) : ComposeTestRule by composeTestRule {

    @get:WorkerThread
    val activityScenario: ActivityScenario<InternalAppyxTestActivity> by lazy {
        val awaitNode = CountDownLatch(1)
        AppyxTestActivity.composableView = { activity ->
            decorator {
                NodeHost(
                    lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                    integrationPoint = activity.appyxV2IntegrationPoint,
                ) { nodeContext ->
                    node = nodeFactory.create(nodeContext)
                    awaitNode.countDown()
                    node
                }
            }
        }
        val scenario = ActivityScenario.launch(InternalAppyxTestActivity::class.java)
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
