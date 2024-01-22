package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.platform.LocalOnBackPressedDispatcherOwner
import com.bumble.appyx.navigation.platform.OnBackPressedDispatcher
import com.bumble.appyx.navigation.platform.OnBackPressedDispatcherOwner
import com.bumble.appyx.navigation.platform.PlatformLifecycleRegistry
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@Suppress("ComposableParamOrder") // detekt complains as 'factory' param isn't a pure lambda
@Composable
fun <N : Node<*>> IosNodeHost(
    onBackPressedEvents: Flow<Unit>,
    modifier: Modifier = Modifier,
    integrationPoint: IntegrationPoint,
    customisations: NodeCustomisationDirectory = remember { NodeCustomisationDirectoryImpl(null) },
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

    val onBackPressedDispatcherOwner = remember {
        object : OnBackPressedDispatcherOwner {
            override val onBackPressedDispatcher: OnBackPressedDispatcher =
                OnBackPressedDispatcher { integrationPoint.handleUpNavigation() }
        }
    }

    val scope = rememberCoroutineScope()
    LaunchedEffect(onBackPressedEvents) {
        scope.launch {
            onBackPressedEvents.collect {
                onBackPressedDispatcherOwner.onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    CompositionLocalProvider(LocalOnBackPressedDispatcherOwner provides onBackPressedDispatcherOwner) {
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
}

private fun Double.pixelsToDp(): Double {
    val mainScreen = UIScreen.mainScreen
    val scale = mainScreen.scale
    return this / scale
}
