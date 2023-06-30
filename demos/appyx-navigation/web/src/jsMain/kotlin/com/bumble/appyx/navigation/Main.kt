package com.bumble.appyx.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import com.bumble.appyx.navigation.integration.ScreenSize
import com.bumble.appyx.navigation.node.container.ContainerNode
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import com.bumble.navigation.integrationpoint.WebNodeHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    val events: Channel<Unit> = Channel()
    onWasmReady {
        Window("Navigation Demo") {
//            val requester = remember { FocusRequester() }
            var screenSize = remember { ScreenSize(0.dp, 0.dp) }
            val eventScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

            AppyxSampleAppTheme {

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { screenSize = ScreenSize(it.width.dp, it.height.dp) }
                        .onKeyEvent {
                            onKeyEvent(it, events, eventScope)
                        }
                ) {
                    Column {
                        WebNodeHost(
                            screenSize = screenSize,
                            onBackPressedEvents = events.receiveAsFlow(),
                        ) { buildContext ->
                            ContainerNode(
                                buildContext = buildContext,
                            )
                        }
                    }
                }
            }

//            LaunchedEffect(Unit) {
//                requester.requestFocus()
//            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun onKeyEvent(
    keyEvent: KeyEvent,
    events: Channel<Unit>,
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
): Boolean =
    when {
        keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace -> {
            coroutineScope.launch { events.send(Unit) }
            true
        }

        else -> false
    }
