package com.github.zsoltk.composeribs.client.backstack

import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

class BackStackExampleNode(
    savedStateMap: SavedStateMap?,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Child("A"),
        savedStateMap = savedStateMap,
    )
) : Node<Routing>(
    routingSource = backStack,
    savedStateMap = savedStateMap,
) {

    sealed class Routing : Parcelable {
        @Parcelize
        data class Child(val name: String) : Routing()
    }

    override fun resolve(routing: Routing, savedStateMap: SavedStateMap?): Node<*> =
        when (routing) {
            is Child -> ChildNode(routing.name, savedStateMap)
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
                        .fillMaxHeight(0.60f)
                ) {
                    Subtree(routingSource = backStack, transitionHandler = BackStackSlider()) {
                        children<Routing> { transitionModifier, child ->
                            Box(modifier = transitionModifier) {
                                child()
                            }
                        }
                    }
                }

                val selectedRadioButton = rememberSaveable { mutableStateOf("A") }
                Row {
                    listOf("A", "B", "C", "D").forEach {
                        Row {
                            RadioButton(
                                selected = it == selectedRadioButton.value,
                                onClick = { selectedRadioButton.value = it }
                            )
                            Text(text = it)
                            Spacer(modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Row {
                    Button(onClick = { backStack.push(Child(selectedRadioButton.value)) }) {
                        Text(text = "Push")
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    val popAllowed by backStack.canHandleBackPress.collectAsState()
                    Button(
                        onClick = { backStack.pop() },
                        enabled = popAllowed,
                    ) {
                        Text(text = "Pop")
                    }
                }
            }
        }
    }
}
