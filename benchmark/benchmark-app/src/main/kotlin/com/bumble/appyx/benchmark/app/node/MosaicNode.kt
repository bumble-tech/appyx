@file:Suppress("MagicNumber")

package com.bumble.appyx.benchmark.app.node

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumble.appyx.benchmark.app.mosaic.MosaicComponent
import com.bumble.appyx.benchmark.app.mosaic.MosaicConfig
import com.bumble.appyx.benchmark.app.mosaic.MosaicPiece
import com.bumble.appyx.benchmark.app.mosaic.operation.assemble
import com.bumble.appyx.benchmark.app.mosaic.operation.carousel
import com.bumble.appyx.benchmark.app.mosaic.operation.flip
import com.bumble.appyx.benchmark.app.mosaic.operation.scatter
import com.bumble.appyx.benchmark.app.ui.FlashCard
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.KEYFRAME
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import kotlin.random.Random

private val animationSpec = spring<Float>(
    stiffness = Spring.StiffnessVeryLow / 15,
    dampingRatio = Spring.DampingRatioNoBouncy
)

class MosaicNode(
    nodeContext: NodeContext,
    private val config: MosaicConfig,
    private val mosaic: MosaicComponent = MosaicComponent(
        gridRows = config.rows,
        gridCols = config.columns,
        pieces = IntRange(0, config.rows * config.columns - 1)
            .shuffled(Random(123))
            .mapIndexed { sequentialIdx, shuffledIdx ->
                MosaicPiece(
                    i = shuffledIdx % config.columns,
                    j = shuffledIdx / config.columns,
                    entryId = sequentialIdx
                )
            },
        savedStateMap = nodeContext.savedStateMap,
        defaultAnimationSpec = animationSpec
    )
) : ParentNode<MosaicPiece>(
    nodeContext = nodeContext,
    appyxComponent = mosaic
) {

    override fun buildChildNode(mosaicPiece: MosaicPiece, nodeContext: NodeContext): Node =
        node(nodeContext) { modifier ->
            Box(
                modifier = modifier
                    .fillMaxWidth(1f / config.columns)
                    .fillMaxHeight(1f / config.rows)
            ) {
                FlashCard(
                    front = { modifier ->
                        Box(
                            modifier = modifier
                                .fillMaxSize()
                                .background(color = Color.Yellow)
                        )
                    },
                    back = { modifier ->
                        Box(
                            modifier = modifier
                                .fillMaxSize()
                                .background(color = Color.Blue)
                        )
                    }
                )
            }
        }

    @Composable
    override fun Content(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp),
        ) {
            AppyxNavigationContainer(
                appyxComponent = mosaic,
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(1f * config.columns / config.rows)
                    .background(Color.DarkGray)
            )
            Controls(
                modifier = Modifier.align(BottomCenter)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(8.dp)
            ) {
                val isIdle by mosaic.isModelIdle.collectAsState()
                Text(
                    if (isIdle) "Idle" else "Executing"
                )
            }
        }
    }


    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun Controls(
        modifier: Modifier = Modifier,
    ) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { mosaic.scatter() }) {
                Text("Scatter")
            }
            Button(onClick = { mosaic.assemble() }) {
                Text("Assemble")
            }
            Button(onClick = { mosaic.flip(KEYFRAME, tween(10000)) }) {
                Text("Flip")
            }
            Button(onClick = { mosaic.carousel() }) {
                Text("Carousel")
            }
        }
    }
}
