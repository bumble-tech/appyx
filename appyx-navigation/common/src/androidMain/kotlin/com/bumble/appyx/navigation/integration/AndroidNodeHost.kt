package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl


/**
 * Composable function to host [Node].
 *
 * This wrapper uses [LocalConfiguration] to provide [ScreenSize] automatically.
 */
@Suppress("ComposableParamOrder") // detekt complains as 'factory' param isn't a pure lambda
@Composable
fun <N : Node> NodeHost(
    lifecycle: Lifecycle,
    integrationPoint: IntegrationPoint,
    modifier: Modifier = Modifier,
    customisations: NodeCustomisationDirectory = remember { NodeCustomisationDirectoryImpl() },
    factory: NodeFactory<N>
) {
    NodeHost(
        lifecycle = lifecycle,
        integrationPoint = integrationPoint,
        modifier = modifier,
        customisations = customisations,
        screenSize = ScreenSize(
            LocalConfiguration.current.screenWidthDp.dp,
            LocalConfiguration.current.screenHeightDp.dp,
        ),
        factory = factory,
    )
}
