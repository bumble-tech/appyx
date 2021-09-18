package com.github.zsoltk.composeribs.client.backstack

import androidx.compose.animation.core.tween
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
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode.Routing
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode.Routing.Child
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.SubtreeController
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider
import kotlin.random.Random

class BackStackExampleNode(
    private val backStack: BackStack<Routing> = BackStack(initialElement = Child(0))
) : Node<Routing>(
    subtreeController = SubtreeController(
        routingSource = backStack,
        transitionHandler = BackStackSlider(
            transitionSpec = { tween(1500) }
        )
    )
) {

    sealed class Routing {
        data class Child(val counter: Int) : Routing()
    }

    override fun resolve(routing: Routing): Node<*> =
        when (routing) {
            is Child -> ChildNode(routing.counter)
        }

    @Composable
    override fun View() {
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
                Button(
                    onClick = { backStack.pop()},
                    enabled = backStack.elements.size > 1
                ) {
                    Text(text = "Pop routing")
                }
            }
        }
    }
}
