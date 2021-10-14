package com.github.zsoltk.composeribs.client.backstack

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.StateObject
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode.Routing
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode.Routing.Child
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack

class BackStackExampleNode(
    private val backStack: BackStack<Routing> = BackStack(initialElement = Child(0))
) : Node<Routing>(
    routingSource = backStack,
) {

    sealed class Routing {
        data class Child(val counter: Int) : Routing()
    }

    override fun resolve(routing: Routing): Node<*> =
        when (routing) {
            is Child -> ChildNode(routing.counter)
        }

    @Composable
    override fun View(foo: StateObject) {
        Column(modifier = Modifier
            .height(200.dp)
            .padding(24.dp)
        ) {
            Text("Back stack example placeholder")

//            Column(Modifier.padding(24.dp)) {
//            Box(
//                Modifier
//                    .padding(top = 12.dp, bottom = 12.dp)
//                    .fillMaxWidth()
//                    .fillMaxHeight(0.75f)
//            ) {
//                // placeholder<Child1>()
//                // placeholder<Child2>()
//                placeholder<Routing>()
//            }
//
//            Row {
//                Button(onClick = { backStack.push(Child(Random.nextInt(9999))) }) {
//                    Text(text = "Push routing")
//                }
//                Spacer(modifier = Modifier.size(12.dp))
//                Button(
//                    onClick = { backStack.pop()},
//                    enabled = backStack.elementsObservable.size > 1
//                ) {
//                    Text(text = "Pop routing")
//                }
//            }
        }
    }
}
