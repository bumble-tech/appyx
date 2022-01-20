package com.bumble.appyx.v2.client.tiles

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.bumble.appyx.v2.client.child.ChildNode
import com.bumble.appyx.v2.client.tiles.TilesExampleNode.Routing
import com.bumble.appyx.v2.client.tiles.TilesExampleNode.Routing.Child1
import com.bumble.appyx.v2.client.tiles.TilesExampleNode.Routing.Child2
import com.bumble.appyx.v2.client.tiles.TilesExampleNode.Routing.Child3
import com.bumble.appyx.v2.client.tiles.TilesExampleNode.Routing.Child4
import com.bumble.appyx.v2.core.children.whenChildrenAttached
import com.bumble.appyx.v2.core.composable.Child
import com.bumble.appyx.v2.core.composable.visibleChildrenAsState
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.source.tiles.Tiles
import com.bumble.appyx.v2.core.routing.source.tiles.operation.removeSelected
import com.bumble.appyx.v2.core.routing.source.tiles.operation.toggleSelection
import com.bumble.appyx.v2.core.routing.source.tiles.rememberTilesTransitionHandler

class TilesExampleNode(
    buildContext: BuildContext,
    private val tiles: Tiles<Routing> = Tiles(
        initialElements = listOf(
            Child1, Child2, Child3, Child4
        )
    ),
) : ParentNode<Routing>(
    routingSource = tiles,
    buildContext = buildContext,
) {

    enum class Routing {
        Child1, Child2, Child3, Child4,
    }

    init {
        whenChildrenAttached { commonLifecycle: Lifecycle, child1: ChildNode, child2: ChildNode ->
            commonLifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    Log.d("TilesExampleNode", "Children $child1 and $child2 were connected")
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    Log.d("TilesExampleNode", "Children $child1 and $child2 were disconnected")
                }
            })
        }
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            Child1 -> ChildNode("1", buildContext)
            Child2 -> ChildNode("2", buildContext)
            Child3 -> ChildNode("3", buildContext)
            Child4 -> ChildNode("4", buildContext)
        }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Button(
                onClick = { tiles.removeSelected() },
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Text(text = "Remove selected")
            }

            val elements by tiles.visibleChildrenAsState()
            LazyVerticalGrid(
                cells = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(top = 60.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(elements) { element ->
                    Child(
                        routingElement = element,
                        transitionHandler = rememberTilesTransitionHandler()
                    ) { child, _ ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clickable {
                                tiles.toggleSelection(element.key)
                            }
                        ) {
                            child()
                        }
                    }
                }
            }

        }
    }
}
