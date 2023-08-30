package com.bumble.appyx.navigation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.integration.NodeFactory
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.integration.ScreenSize
import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.platform.PlatformLifecycleRegistry
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@Composable
fun <N : Node> IOSNodeHost(
    modifier: Modifier = Modifier,
    integrationPoint: IntegrationPoint = remember { MainIntegrationPoint() },
    customisations: NodeCustomisationDirectory = remember { NodeCustomisationDirectoryImpl() },
    factory: NodeFactory<N>,
) {
    val platformLifecycleRegistry = remember {
        PlatformLifecycleRegistry()
    }
    val mainScreen = UIScreen.mainScreen
    val screenBounds = mainScreen.bounds

    // Calculate the width and height in dp
    val dpWidth = screenBounds.useContents { size.width }.pixelsToDp()
    val dpHeight = screenBounds.useContents { size.height }.pixelsToDp()
    NodeHost(
        lifecycle = platformLifecycleRegistry,
        integrationPoint = integrationPoint,
        modifier = modifier,
        customisations = customisations,
        screenSize = ScreenSize(
            widthDp = dpWidth.dp,
            heightDp = dpHeight.dp,
        ),
        factory = factory,
    )
}

private fun Double.pixelsToDp(): Double {
    val mainScreen = UIScreen.mainScreen
    val scale = mainScreen.scale
    return this / scale
}