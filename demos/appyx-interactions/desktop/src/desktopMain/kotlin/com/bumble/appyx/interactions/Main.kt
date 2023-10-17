@file:Suppress("MatchingDeclarationName")
package com.bumble.appyx.interactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.bumble.appyx.interactions.widgets.Widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class Events {
    object OnUpClicked : Events()
    object OnDownClicked : Events()
}

fun main() = application {
    val events: Channel<Events> = Channel()
    val windowState = rememberWindowState(size = DpSize(480.dp, 658.dp))
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        undecorated = true,
        transparent = true,
        resizable = false,
        onKeyEvent = { onKeyEvent(it, events) },
    ) {
        var size by remember { mutableStateOf(IntSize.Zero) }
        Column(modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
            .padding(8.dp)
        ) {
            TitleBar(
                text = "Desktop widgets",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(BiasAlignment.Horizontal(-0.3f))
            )
            Widgets(
                events = events.receiveAsFlow(),
                screenWidthPx = size.width,
                screenHeightPx = size.height,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(320.dp)
            )
        }
    }
}

@Composable
private fun FrameWindowScope.TitleBar(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0.9f, 0.9f, 0.9f),
) {
    WindowDraggableArea(
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(32.dp)
                .background(
                    brush = Brush.linearGradient(
                        0f to color.copy(alpha = 0.6f),
                        0.2f to color,
                        0.8f to color,
                        1f to color.copy(alpha = 0.6f),
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                text = text,
                color = Color.Black.copy(alpha = 0.9f),
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

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
