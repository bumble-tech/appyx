package com.github.zsoltk.composeribs.client.tiles

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.StateObject
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing.Child1
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing.Child2
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing.Child3
import com.github.zsoltk.composeribs.client.tiles.TilesExampleNode.Routing.Child4
import com.github.zsoltk.composeribs.core.Node

//import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles
//import com.github.zsoltk.composeribs.core.routing.source.tiles.TilesTransitionHandler

class TilesExampleNode(
//    private val tiles: Tiles<Routing> = Tiles(initialElements = listOf(
//        Child1, Child2, Child3, Child4
//    ))
) : Node<Routing>(
//    subtreeController = SubtreeController(
        routingSource = null // tiles,
//        transitionHandler = TilesTransitionHandler(
//            transitionSpec = { tween(1500) }
//        )
//    )
) {

    sealed class Routing {
        object Child1 : Routing()
        object Child2 : Routing()
        object Child3 : Routing()
        object Child4 : Routing()
    }

    override fun resolve(routing: Routing): Node<*> =
        when (routing) {
            Child1 -> ChildNode(1)
            Child2 -> ChildNode(2)
            Child3 -> ChildNode(3)
            Child4 -> ChildNode(4)
        }

    @Composable
    override fun View(foo: StateObject) {
        Text("Tiles placeholder")

//        val modifier: (RoutingKey<Routing>) -> Modifier = {
//            Modifier
//                .widthIn(max = 150.dp)
//                .heightIn(max = 250.dp)
//                .clickable { tiles.toggleSelection(it) }
//        }
//
//        Box(Modifier.fillMaxSize()) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight(0.6f)
//                    .padding(24.dp),
//            ) {
//                Text("Container")
//                Row(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxWidth()
//                        .padding(top = 12.dp, bottom = 6.dp),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//
//                    placeholder<Child1>(modifier)
//                    placeholder<Child2>(modifier)
//                }
//                Row(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxWidth()
//                        .padding(top = 6.dp, bottom = 12.dp),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    placeholder<Child3>(modifier)
//                    placeholder<Child4>(modifier)
//
////                placeholder<Routing.Child>(modifier) { it.i % 4 == 3 }
////                placeholder<Routing.Child>(modifier) { it.i % 4 == 2 }
//                }
//
//                Button(onClick = { tiles.removeSelected() } ) {
//                    Text("Remove selected")
//                }
//            }
//        }
    }
}
