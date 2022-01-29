package com.bumble.appyx.v2.app.composable.graph

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bumble.appyx.v2.app.composable.graph.nodeimpl.TestImpl


@Composable
@ExperimentalComposeUiApi
fun Tree(
    graphNode: GraphNode,
    parentChildrenTopCenters: SnapshotStateMap<Int, Offset> = mutableStateMapOf(),
    idx: Int = 0,
    gapHeight: Dp = 40.dp,
    lineColor: Color = if (isSystemInDarkTheme()) Color.Black else Color(0xFFDDDDDD),
    activeLineColor: Color = MaterialTheme.colors.primary,
    strokeWidth: Float = 7f,
) {
    val children = graphNode.children()
    val wrapperOffset = remember { mutableStateOf(Offset(0f, 0f)) }
    val thisNodeBottomCenter = remember { mutableStateOf(Offset(0f, 0f)) }
    val thisChildrenTopCenters = remember { mutableStateMapOf<Int, Offset>() }

    Column(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .onGloballyPositioned {
                wrapperOffset.value = it.positionInParent()
            }
    ) {
        graphNode.View(
            modifier = Modifier
                .align(CenterHorizontally)
                .zIndex(1f)
                .onGloballyPositioned {
                    val localOffset = it.positionInParent()
                    val positionInParent = wrapperOffset.value + localOffset
                    val thisNodeTopCenter = Offset(
                        x = positionInParent.x + it.size.width / 2,
                        y = positionInParent.y
                    )
                    thisNodeBottomCenter.value = Offset(
                        x = localOffset.x + it.size.width / 2f,
                        y = localOffset.y + it.size.height
                    )

                    parentChildrenTopCenters[idx] = thisNodeTopCenter
                }
        )

        if (children.isNotEmpty()) {
            Row {
                val colors = children.map { child ->
                    animateColorAsState(targetValue = if (child.isActive.value) activeLineColor else lineColor)
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(gapHeight)
                ) {
                    thisChildrenTopCenters.values.forEachIndexed { idx, childTopCenter ->
                            drawLine(
                                start = Offset(thisNodeBottomCenter.value.x, -20f), // TODO this assumes child size
                                end = Offset(childTopCenter.x, size.height + 20), // TODO this assumes child size
                                color = colors[idx].value,
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round
                            )
                    }
                }
            }

            Row {
                children.forEachIndexed { idx, child ->
                    Tree(
                        graphNode = child,
                        parentChildrenTopCenters = thisChildrenTopCenters,
                        idx = idx,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
@ExperimentalComposeUiApi
fun GraphNodePreview() {
    Tree(TestImpl(1, 4))
}
