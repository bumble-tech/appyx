package com.bumble.appyx.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.integration.ScreenSize
import com.bumble.appyx.navigation.node.container.ContainerNode
import com.bumble.appyx.navigation.platform.PlatformLifecycleRegistry
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import com.bumble.navigation.integrationpoint.MainIntegrationPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

sealed class Events {
    object OnUpClicked : Events()
    object OnDownClicked : Events()
}

fun main() = application {
    val events: Channel<Events> = Channel()
    val windowState = rememberWindowState(size = DpSize(480.dp, 658.dp))
    val screenSize = remember {
        derivedStateOf { windowState.size.run { ScreenSize(width, height) } }
    }
    val platformLifecycleRegistry = remember {
        PlatformLifecycleRegistry()
    }
    val integrationPoint = remember {
        MainIntegrationPoint(onNavigateUp = { false })
    }
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        onKeyEvent = { onKeyEvent(it, events) },
    ) {
        AppyxSampleAppTheme {
            // A surface container using the 'background' color from the theme
            Surface(color = MaterialTheme.colorScheme.background) {
                Column {
                    NodeHost(
                        lifecycle = platformLifecycleRegistry,
                        integrationPoint = integrationPoint,
                        screenSize = screenSize.value,
                    ) {
                        ContainerNode(
                            buildContext = it,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun onKeyEvent(
    keyEvent: KeyEvent,
    events: Channel<Events>,
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
): Boolean =
    when {
        keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionUp -> {
            coroutineScope.launch { events.send(Events.OnUpClicked) }
            true
        }

        keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionDown -> {
            coroutineScope.launch { events.send(Events.OnDownClicked) }
            true
        }

        else -> false
    }
