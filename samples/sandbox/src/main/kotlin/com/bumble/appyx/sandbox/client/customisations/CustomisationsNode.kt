package com.bumble.appyx.sandbox.client.customisations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.sandbox.TextButton

class CustomisationsNode(
    buildContext: BuildContext,
) : Node(buildContext = buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton("Push default node") {
                    integrationPoint.activityStarter.startActivity {
                        createViewCustomisationsActivityIntent(
                            context = this,
                            hasCustomisedView = false
                        )
                    }
                }
                TextButton("Push node with customised view") {
                    integrationPoint.activityStarter.startActivity {
                        createViewCustomisationsActivityIntent(
                            context = this,
                            hasCustomisedView = true
                        )
                    }
                }
            }
        }
    }
}
