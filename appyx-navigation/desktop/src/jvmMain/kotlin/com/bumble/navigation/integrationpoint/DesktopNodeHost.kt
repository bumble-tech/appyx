package com.bumble.navigation.integrationpoint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.WindowState
import com.bumble.appyx.navigation.integration.NodeFactory
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.integration.ScreenSize
import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.platform.LocalOnBackPressedDispatcherOwner
import com.bumble.appyx.navigation.platform.OnBackPressedDispatcher
import com.bumble.appyx.navigation.platform.OnBackPressedDispatcherOwner
import com.bumble.appyx.navigation.platform.PlatformLifecycleRegistry
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Composable function to host [Node].
 *
 * This convenience wrapper uses [WindowState] to provide [ScreenSize] and provides an
 * [OnBackPressedDispatcherOwner] hooked up to the [.onBackPressedEvents] flow to simplify
 * implementing the global "go back" functionality that is a common concept in the Appyx framework.
 */
@Suppress("ComposableParamOrder") // detekt complains as 'factory' param isn't a pure lambda
@Composable
fun <N : Node> NodeHost(
    windowState: WindowState,
    modifier: Modifier = Modifier,
    onBackPressedEvents: Flow<Unit>,
    integrationPoint: IntegrationPoint = remember { MainIntegrationPoint() },
    customisations: NodeCustomisationDirectory = remember { NodeCustomisationDirectoryImpl() },
    factory: NodeFactory<N>
) {
    val screenSize = remember {
        derivedStateOf { windowState.size.run { ScreenSize(width, height) } }
    }
    val platformLifecycleRegistry = remember {
        PlatformLifecycleRegistry()
    }
    val scope = rememberCoroutineScope()
    val onBackPressedDispatcherOwner = remember {
        object : OnBackPressedDispatcherOwner {
            override val onBackPressedDispatcher: OnBackPressedDispatcher =
                OnBackPressedDispatcher { integrationPoint.handleUpNavigation() }

            init {
                scope.launch {
                    onBackPressedEvents.collect {
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
    }
    CompositionLocalProvider(LocalOnBackPressedDispatcherOwner provides onBackPressedDispatcherOwner) {
        NodeHost(
            lifecycle = platformLifecycleRegistry,
            integrationPoint = integrationPoint,
            modifier = modifier,
            customisations = customisations,
            screenSize = screenSize.value,
            factory = factory,
        )
    }
}