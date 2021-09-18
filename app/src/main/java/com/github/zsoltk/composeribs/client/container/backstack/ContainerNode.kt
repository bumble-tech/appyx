package com.github.zsoltk.composeribs.client.container.backstack

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.child.ChildBuilder
import com.github.zsoltk.composeribs.client.container.backstack.ContainerNode.Routing
import com.github.zsoltk.composeribs.client.container.backstack.ContainerNode.Routing.Child
import com.github.zsoltk.composeribs.core.InnerNode
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import kotlin.random.Random

class ContainerNode(
    private val backStack: BackStack<Routing>
) : InnerNode<Routing>() {

    sealed class Routing {
        data class Child(val counter: Int) : Routing()
    }

    private val childBuilder = ChildBuilder()

    override fun invoke(routing: Routing): Node<*> =
        when (routing) {
            is Child -> childBuilder.build(routing.counter)
        }

    @Composable
    override fun Compose() {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
        ) {
            Text("Container")

//            Column(Modifier.padding(24.dp)) {
            Box(
                Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
            ) {
                // placeholder<Child1>()
                // placeholder<Child2>()
                placeholder<Routing>()
            }

            Row {
                Button(onClick = { backStack.push(Child(Random.nextInt(9999))) }) {
                    Text(text = "Push routing")
                }
                Spacer(modifier = Modifier.size(12.dp))
                Button(onClick = { backStack.pop()} ) {
                    Text(text = "Pop routing")
                }
            }
        }
    }
}
