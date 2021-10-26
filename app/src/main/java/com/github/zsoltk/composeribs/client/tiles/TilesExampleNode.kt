package com.github.zsoltk.composeribs.client.tiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing.*
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.Subtree
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
) : Node<Routing>(
    routingSource = tiles,
    buildContext = buildContext,
) {

    enum class Routing {
        Child1, Child2, Child3, Child4,
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node<*> =
        when (routing) {
            Child1 -> ChildNode(1, buildContext)
            Child2 -> ChildNode(2, buildContext)
            Child3 -> ChildNode(3, buildContext)
            Child4 -> ChildNode(4, buildContext)
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

            Subtree(routingSource = tiles, transitionHandler = handler) {
                children<Routing> { transitionModifier, child ->
                    Box(modifier = transitionModifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable {
                            // TODO No access to child id
                            // tiles.toggleSelection(child)
                        }
                    ) {
                        child()
                    }
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
