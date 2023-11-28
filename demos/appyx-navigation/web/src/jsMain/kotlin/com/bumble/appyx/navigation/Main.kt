package com.bumble.appyx.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.bumble.appyx.demos.appyxSample
import com.bumble.appyx.demos.common.color_primary
import com.bumble.appyx.navigation.integration.ScreenSize
import com.bumble.appyx.navigation.integration.WebNodeHost
import com.bumble.appyx.navigation.navigator.LocalNavigator
import com.bumble.appyx.navigation.navigator.Navigator
import com.bumble.appyx.navigation.node.root.RootNode
import com.bumble.appyx.navigation.ui.AppyxSampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val events: Channel<Unit> = Channel()
    val navigator = Navigator()
    appyxSample {
        CanvasBasedWindow("Appyx navigation demo") {
            CakeApp(events, navigator)
        }
    }
}

private val containerShape = RoundedCornerShape(8)

@Composable
private fun CakeApp(events: Channel<Unit>, navigator: Navigator) {
    AppyxSampleAppTheme(darkTheme = true, themeTypography = webTypography) {
        val requester = remember { FocusRequester() }
        var hasFocus by remember { mutableStateOf(false) }

        var screenSize by remember { mutableStateOf(ScreenSize(0.dp, 0.dp)) }
        val eventScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Main) }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { screenSize = ScreenSize(it.width.dp, it.height.dp) }
                .onKeyEvent {
                    onKeyEvent(it, events, eventScope)
                }
                .focusRequester(requester)
                .focusable()
                .onFocusChanged { hasFocus = it.hasFocus },
            color = MaterialTheme.colorScheme.background,
        ) {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                BlackContainer {
                    WebNodeHost(
                        screenSize = screenSize,
                        onBackPressedEvents = events.receiveAsFlow(),
                    ) { buildContext ->
                        RootNode(
                            buildContext = buildContext,
                            plugins = listOf(navigator)
                        )
                    }
                }
            }
        }

        if (!hasFocus) {
            LaunchedEffect(Unit) {
                requester.requestFocus()
            }
        }
    }
}

@Composable
private fun BlackContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(0.56f)
                .border(4.dp, color_primary, containerShape)
                .clip(containerShape)
        ) {
            content()
        }
    }
}

private fun onKeyEvent(
    keyEvent: KeyEvent,
    events: Channel<Unit>,
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
): Boolean =
    when {
        keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Backspace -> {
            coroutineScope.launch { events.send(Unit) }
            true
        }

        else -> false
    }
