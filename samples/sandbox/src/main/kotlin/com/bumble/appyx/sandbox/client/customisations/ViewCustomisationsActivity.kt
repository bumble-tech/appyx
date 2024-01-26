package com.bumble.appyx.sandbox.client.customisations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.sandbox.ui.AppyxSandboxTheme
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl
import com.bumble.appyx.utils.customisations.put

class ViewCustomisationsActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hasCustomisedView: Boolean = intent.getBooleanExtra(HAS_CUSTOMISED_VIEW, false)
        setContent {
            AppyxSandboxTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column {
                        NodeHost(
                            integrationPoint = appyxV1IntegrationPoint,
                            customisations = getCustomisations(hasCustomisedView)
                        ) {
                            ViewCustomisationExampleBuilder().build(buildContext = it)
                        }
                    }
                }
            }
        }
    }

    private fun getCustomisations(hasCustomisedView: Boolean): NodeCustomisationDirectoryImpl {
        val nodeCustomisationDirectory = NodeCustomisationDirectoryImpl()
        if (hasCustomisedView) {
            nodeCustomisationDirectory.apply {
                put {
                    ViewCustomisationExampleNode.Customisations(viewFactory = {
                        ViewCustomisationExampleCustomisedView()
                    })
                }
            }
        }

        return nodeCustomisationDirectory
    }
}

fun createViewCustomisationsActivityIntent(context: Context, hasCustomisedView: Boolean = false) =
    Intent(context, ViewCustomisationsActivity::class.java).apply {
        putExtra(HAS_CUSTOMISED_VIEW, hasCustomisedView)
    }

private const val HAS_CUSTOMISED_VIEW = "has_customised_view"
