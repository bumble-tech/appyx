package com.bumble.appyx.sandbox.client.explicitnavigation

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.core.plugin.NodeReadyObserver
import com.bumble.appyx.sandbox.client.explicitnavigation.treenavigator.Navigator
import com.bumble.appyx.sandbox.ui.AppyxSandboxTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ExplicitNavigationExampleActivity : NodeActivity(), Navigator {

    lateinit var rootNode: RootNode

    private var job: Job? = null

    fun handleDeepLink(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            workflowFactory.invoke(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    private val workflowFactory: (Intent) -> Unit? = {
        when {
            // adb shell am start -a "android.intent.action.VIEW" -d "appyx-sample://workflow1"
            (it.data?.host == "workflow1") -> workflow1()

            // adb shell am start -a "android.intent.action.VIEW" -d "appyx-sample://workflow2"
            (it.data?.host == "workflow2") -> workflow2()

            // adb shell am start -a "android.intent.action.VIEW" -d "appyx-sample://workflow3"
            (it.data?.host == "workflow3") -> navigateToChildOne()

            else -> null
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppyxSandboxTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        NodeHost(integrationPoint = appyxIntegrationPoint) {
                            RootNode(
                                buildContext = it,
                                navigator = this@ExplicitNavigationExampleActivity,
                                plugins = listOf(object : NodeReadyObserver<RootNode> {
                                    override fun init(node: RootNode) {
                                        rootNode = node
                                        handleDeepLink(intent = intent)
                                    }
                                })
                            )
                        }
                    }
                }
            }
        }
    }

    private fun executeNavigation(navigationAction: suspend () -> Unit) {
        job?.cancel()
        job = lifecycleScope.launch {
            navigationAction()
        }
    }

    // waits until user navigates to child two and immediately attached grand child two
    private fun workflow1() {
        executeNavigation {
            rootNode
                .attachChildTwo()
                .waitForGrandchildTwoAttached()
                .printLifecycleState()
        }
    }


    // waits until user navigates to child two and immediately attached grand child two and prints
    // its lifecycle state
    private fun workflow2() {
        executeNavigation {
            rootNode
                .waitForChildTwoAttached()
                .attachGrandchildTwo()
                .printLifecycleState()
        }
    }

    override fun navigateToChildOne() {
        executeNavigation {
            rootNode.attachChildOne()
        }
    }
}
