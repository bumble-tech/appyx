package com.bumble.appyx.sandbox.client.workflow

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.core.plugin.NodeAware
import com.bumble.appyx.sandbox.ui.AppyxSandboxTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WorkflowExampleActivity : NodeActivity() {

    lateinit var rootNode: RootNode

    private var job: Job? = null

    fun handleDeepLink(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            job?.cancel()
            job = workflowFactory.invoke(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    private val workflowFactory: (Intent) -> Job? = {
        when {
            // adb shell am start -a "android.intent.action.VIEW" -d "appyx-sample://workflow1"
            (it.data?.host == "workflow1") -> executeWorkflow1()

            // adb shell am start -a "android.intent.action.VIEW" -d "appyx-sample://workflow2"
            (it.data?.host == "workflow2") -> executeWorkflow2()
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
                        NodeHost(integrationPoint = integrationPoint) {
                            RootNode(
                                buildContext = it,
                                plugins = listOf(object : NodeAware<RootNode> {
                                    override fun init(node: RootNode) {
                                        rootNode = node
                                        handleDeepLink(intent = intent)
                                    }

                                    override val node: RootNode
                                        get() = rootNode
                                })
                            )
                        }
                    }
                }
            }
        }
    }

    private fun executeWorkflow1(): Job {
        return lifecycleScope.launch {
            rootNode
                .waitForChildTwoAttached()
                .attachGrandchildTwo()
        }
    }


    private fun executeWorkflow2(): Job {
        return lifecycleScope.launch {
            rootNode
                .attachChildTwo()
                .attachGrandchildTwo()
                .printLifecycleEvent()
        }
    }
}
