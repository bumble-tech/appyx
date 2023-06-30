package com.bumble.appyx.components.experimental.puzzle15.ui

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
import com.bumble.appyx.components.experimental.puzzle15.Puzzle15
import com.bumble.appyx.components.experimental.puzzle15.Puzzle15Model
import com.bumble.appyx.components.experimental.puzzle15.operation.Shuffle
import com.bumble.appyx.components.experimental.puzzle15.operation.Swap
import com.bumble.appyx.components.experimental.puzzle15.operation.Swap.Direction.DOWN
import com.bumble.appyx.components.experimental.puzzle15.operation.Swap.Direction.LEFT
import com.bumble.appyx.components.experimental.puzzle15.operation.Swap.Direction.RIGHT
import com.bumble.appyx.components.experimental.puzzle15.operation.Swap.Direction.UP
import com.bumble.appyx.interactions.core.ui.helper.AppyxComponentSetup
import com.bumble.appyx.interactions.sample.Children

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Puzzle15Ui(
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val model = remember { Puzzle15Model(savedStateMap = null) }

    val puzzle15 = remember {
        Puzzle15(
            scope = coroutineScope,
            model = model
        )
    }

    AppyxComponentSetup(puzzle15)

    Column(
        modifier = modifier.onKeyEvent {
            if (it.type == KeyEventType.KeyDown) {
                when (it.key) {
                    Key.DirectionLeft -> puzzle15.operation(Swap(RIGHT))
                    Key.DirectionUp -> puzzle15.operation(Swap(DOWN))
                    Key.DirectionDown -> puzzle15.operation(Swap(UP))
                    Key.DirectionRight -> puzzle15.operation(Swap(LEFT))
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
                appyxComponent = puzzle15,
            ) { elementUiModel ->
                if (elementUiModel.element.interactionTarget == Puzzle15Model.Tile.EmptyTile) {
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
                                    elementUiModel
                                        .modifier
                                        .background(color = Color.White)
                                )
                                .pointerInput(elementUiModel.element.id) {
                                    this.interceptOutOfBoundsChildEvents = true
                                    detectDragGestures(
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            puzzle15.onDrag(dragAmount, this)
                                        },
                                        onDragEnd = {
                                            puzzle15.onDragEnd()
                                        }
                                    )
                                }
                        ) {
                            Text(
                                text = elementUiModel.element.interactionTarget.textValue(),
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
                                text = elementUiModel.element.interactionTarget.textValue(),
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
                    onClick = { puzzle15.operation(Swap(direction = DOWN)) }
                ) {
                    Text("^")
                }
                Button(
                    onClick = { puzzle15.operation(Swap(direction = LEFT)) }
                ) {
                    Text(">")
                }
                Button(
                    onClick = { puzzle15.operation(Swap(direction = UP)) }
                ) {
                    Text("\u2304")
                }
                Button(
                    onClick = { puzzle15.operation(Swap(direction = RIGHT)) }
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

private fun Puzzle15Model.Tile.textValue() =
    when (this) {
        Puzzle15Model.Tile.Tile1 -> "1"
        Puzzle15Model.Tile.Tile2 -> "2"
        Puzzle15Model.Tile.Tile3 -> "3"
        Puzzle15Model.Tile.Tile4 -> "4"
        Puzzle15Model.Tile.Tile5 -> "5"
        Puzzle15Model.Tile.Tile6 -> "6"
        Puzzle15Model.Tile.Tile7 -> "7"
        Puzzle15Model.Tile.Tile8 -> "8"
        Puzzle15Model.Tile.Tile9 -> "9"
        Puzzle15Model.Tile.Tile10 -> "10"
        Puzzle15Model.Tile.Tile11 -> "11"
        Puzzle15Model.Tile.Tile12 -> "12"
        Puzzle15Model.Tile.Tile13 -> "13"
        Puzzle15Model.Tile.Tile14 -> "14"
        Puzzle15Model.Tile.Tile15 -> "15"
        Puzzle15Model.Tile.EmptyTile -> ""
    }
