package com.github.zsoltk.composeribs.client.tiles

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing.Child1
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing.Child2
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing.Child3
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing.Child4
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.ParentNode
import com.github.zsoltk.composeribs.core.Subtree
import com.github.zsoltk.composeribs.core.children.whenChildrenAttached
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles
import com.github.zsoltk.composeribs.core.routing.source.tiles.TilesTransitionHandler
import com.github.zsoltk.composeribs.core.visibleChildAsState

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

    @Composable
    override fun View() {
        val handler = TilesTransitionHandler()
        Column(modifier = Modifier.fillMaxSize()) {

            /*val children by tiles.childrenAsState()

            children.forEachIndexed { index, routingElement ->

                if (index % 2 == 1) {
                    Text(
                        text = "AD BANNER GOES HERE",
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                    )
                }

                AnimatedChildNode(
                    routingElement = routingElement,
                    transitionHandler = handler,
                ) { modifier, child ->
                    Box(modifier = modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable {
                            tiles.toggleSelection(routingElement.key)
                        }
                    ) {
                        child()
                    }
                }

            }*/

            Subtree(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                routingSource = tiles,
                transitionHandler = handler
            ) {
                children<Routing> { child ->
                    child()
                }
            }

            Button(
                onClick = { tiles.removeSelected() },
                modifier = Modifier.align(CenterHorizontally)
            ) {
                Text(text = "Remove")
            }

            Text(text = "Child1 separately", modifier = Modifier.align(CenterHorizontally))

            val child1 by tiles.visibleChildAsState(Child1::class)
            val c = child1

            /*if (c != null) {
                AnimatedChildNode(
                    routingElement = c,
                    transitionHandler = handler,
                )
            }*/

        }
    }
}
