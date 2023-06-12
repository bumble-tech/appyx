package com.bumble.appyx.components.demos.puzzle15.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.demos.puzzle15.Puzzle15
import com.bumble.appyx.components.demos.puzzle15.Puzzle15Model
import com.bumble.appyx.components.demos.puzzle15.operation.Shuffle
import com.bumble.appyx.components.demos.puzzle15.operation.Swap
import com.bumble.appyx.components.demos.puzzle15.operation.Swap.Direction.Down
import com.bumble.appyx.components.demos.puzzle15.operation.Swap.Direction.Left
import com.bumble.appyx.components.demos.puzzle15.operation.Swap.Direction.Right
import com.bumble.appyx.components.demos.puzzle15.operation.Swap.Direction.Up
import com.bumble.appyx.interactions.AppyxLogger
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.sample.Children

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Puzzle15Ui(
    screenWidthPx: Int,
    screenHeightPx: Int,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    println("Hello Puzzle15")
    val coroutineScope = rememberCoroutineScope()

    val model = remember { Puzzle15Model(savedStateMap = null) }

    val puzzle15 = remember {
        Puzzle15(
            scope = coroutineScope,
            model = model
        )
    }

    InteractionModelSetup(puzzle15)

    Column(
        modifier = modifier.onKeyEvent {
            println("Getting event: $it")
            if (it.type == KeyEventType.KeyDown) {
                when (it.key) {
                    Key.DirectionLeft -> puzzle15.operation(Swap(Right))
                    Key.DirectionUp -> puzzle15.operation(Swap(Down))
                    Key.DirectionDown -> puzzle15.operation(Swap(Up))
                    Key.DirectionRight -> puzzle15.operation(Swap(Left))
                }
            }
            false
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
        ) {
            Children(
                screenWidthPx = screenWidthPx,
                screenHeightPx = screenHeightPx,
                colors = colors,
                interactionModel = puzzle15,
            ) { elementUiModel ->
                if (elementUiModel.element.interactionTarget.value.isEmpty()) {
                    Box(
                        modifier = Modifier.size(60.dp)
                            .then(
                                elementUiModel.modifier
                                    .background(color = Color.Transparent)
                            )
                    )
                } else {
                    val state = model.output.value
                    val element =
                        state.currentTargetState.items.find { it.id == elementUiModel.element.id }
                    val index = state.currentTargetState.items.indexOf(element)
                    val emptyTileIndex = state.currentTargetState.emptyTileIndex
                    if (index == emptyTileIndex - 1 ||
                        index == emptyTileIndex + 1 ||
                        index == emptyTileIndex - 4 ||
                        index == emptyTileIndex + 4
                    ) {
                        Box(
                            modifier = Modifier.size(60.dp)
                                .then(
                                    elementUiModel.modifier
                                        .background(color = Color.White)
                                ).pointerInput(elementUiModel.element.id) {
                                    detectDragGestures(
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            puzzle15.onDrag(dragAmount, this)
                                        },
                                        onDragEnd = {
                                            AppyxLogger.d("drag", "end")
                                            puzzle15.onDragEnd()
                                        }
                                    )
                                }
                        ) {
                            Text(
                                text = elementUiModel.element.interactionTarget.value,
                                modifier = Modifier.align(Alignment.Center),
                                fontSize = 24.sp,
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.size(60.dp)
                                .then(
                                    elementUiModel.modifier
                                        .background(color = Color.White)
                                )
                        ) {
                            Text(
                                text = elementUiModel.element.interactionTarget.value,
                                modifier = Modifier.align(Alignment.Center),
                                fontSize = 24.sp,
                            )
                        }
                    }
                }
            }
        }

        Column {
            Row {
                Button(
                    onClick = { puzzle15.operation(Swap(direction = Down)) }
                ) {
                    Text("^")
                }
                Button(
                    onClick = { puzzle15.operation(Swap(direction = Left)) }
                ) {
                    Text(">")
                }
                Button(
                    onClick = { puzzle15.operation(Swap(direction = Up)) }
                ) {
                    Text("\u2304")
                }
                Button(
                    onClick = { puzzle15.operation(Swap(direction = Right)) }
                ) {
                    Text("<")
                }
            }
            Button(
                onClick = { puzzle15.operation(Shuffle()) }
            ) {
                Text("New Game")
            }
        }
    }
}
