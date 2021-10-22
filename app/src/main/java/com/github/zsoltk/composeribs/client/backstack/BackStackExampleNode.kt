package com.github.zsoltk.composeribs.client.backstack

import android.os.Parcelable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode.Routing
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode.Routing.Child
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.SavedStateMap
import com.github.zsoltk.composeribs.core.Subtree
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.pop
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.push
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

class BackStackExampleNode(
    savedStateMap: SavedStateMap?,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Child(0),
        savedStateMap = savedStateMap,
    )
) : Node<Routing>(
    routingSource = backStack,
    savedStateMap = savedStateMap,
) {

    sealed class Routing : Parcelable {
        @Parcelize
        data class Child(val counter: Int) : Routing()
    }

    override fun resolve(routing: Routing, savedStateMap: SavedStateMap?): Node<*> =
        when (routing) {
            is Child -> ChildNode(routing.counter, savedStateMap)
        }

    @Composable
    override fun View() {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Back stack example placeholder")

            Column(Modifier.padding(24.dp)) {
                Box(
                    Modifier
                        .padding(top = 12.dp, bottom = 12.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.75f)
                ) {
                    Subtree(routingSource = backStack, transitionHandler = BackStackSlider()) {
                        children<Routing> { transitionModifier, child ->
                            Box(modifier = transitionModifier) {
                                child()
                            }
                        }
                    }
                }

                Row {
                    Button(onClick = { backStack.push(Child(Random.nextInt(9999))) }) {
                        Text(text = "Push routing")
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    val popAllowed by backStack.canHandleBackPress.collectAsState()
                    Button(
                        onClick = { backStack.pop() },
                        enabled = popAllowed,
                    ) {
                        Text(text = "Pop routing")
                    }
                }
            }
        }
    }
}
