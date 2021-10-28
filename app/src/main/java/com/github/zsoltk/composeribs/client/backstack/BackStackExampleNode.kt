package com.github.zsoltk.composeribs.client.backstack

import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType.Companion.Number
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode.Operation.*
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode.Routing
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode.Routing.Child
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.SavedStateMap
import com.github.zsoltk.composeribs.core.Subtree
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElements
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.*
import com.google.accompanist.flowlayout.FlowRow
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
        val backStackState = backStack.all.collectAsState()
        val selectedRadioButton = rememberSaveable { mutableStateOf("") }
        val isRadioButtonNeeded = rememberSaveable { mutableStateOf(false) }
        val typedId = rememberSaveable { mutableStateOf("") }
        val isIdNeeded = rememberSaveable { mutableStateOf(false) }
        val selectedOperation = rememberSaveable { mutableStateOf<Operation?>(null) }
        val areThereMissingParams = rememberSaveable { mutableStateOf(true) }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Text("Back stack example placeholder")
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .padding(top = 12.dp, bottom = 12.dp)
                            .fillMaxWidth()
                    ) {
                        Subtree(routingSource = backStack, transitionHandler = BackStackSlider()) {
                            children<Routing> { transitionModifier, child ->
                                Box(modifier = transitionModifier) {
                                    child()
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Child: ", fontWeight = Bold)
                        Row {
                            listOf("A", "B", "C", "D").forEach {
                                Row {
                                    RadioButton(
                                        selected = it == selectedRadioButton.value,
                                        enabled = isRadioButtonNeeded.value,
                                        onClick = { selectedRadioButton.value = it }
                                    )
                                    Text(text = it)
                                    Spacer(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Id: ", fontWeight = Bold)
                        TextField(
                            value = typedId.value,
                            enabled = isIdNeeded.value,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = if (isIdNeeded.value) {
                                    White
                                } else {
                                    LightGray
                                },
                            ),
                            onValueChange = { typedId.value = it },
                            keyboardOptions = KeyboardOptions(keyboardType = Number)
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Operation: ", fontWeight = Bold)
                        FlowRow {
                            values().forEach { operation ->
                                val selected = selectedOperation.value == operation
                                Button(
                                    onClick = {
                                        selectedOperation.value = operation
                                        selectedRadioButton.value = ""
                                        typedId.value = ""
                                        isRadioButtonNeeded.value = operation.radioButtonNeeded
                                        isIdNeeded.value = operation.idNeeded
                                        areThereMissingParams.value = true
                                    },
                                    modifier = Modifier.padding(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = if (selected) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            MaterialTheme.colors.primaryVariant
                                        },
                                        contentColor = MaterialTheme.colors.onPrimary
                                    )
                                ) {
                                    Text(text = operation.label)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Missing params: ", fontWeight = Bold)
                        val textBuilder = mutableListOf<String>()
                        if (selectedOperation.value?.radioButtonNeeded == true && selectedRadioButton.value.isEmpty()) {
                            textBuilder.add("Child")
                            areThereMissingParams.value = true
                        }
                        if (selectedOperation.value?.idNeeded == true && typedId.value.isEmpty()) {
                            textBuilder.add("Id")
                            areThereMissingParams.value = true
                        }
                        if (textBuilder.isEmpty()) {
                            textBuilder.add("None")
                            areThereMissingParams.value = false
                        }
                        Text(text = textBuilder.joinToString(", "))
                    }
                    Button(
                        enabled = selectedOperation.value != null && !areThereMissingParams.value,
                        onClick = {
                            when (selectedOperation.value) {
                                PUSH -> {
                                    backStack.push(Child(selectedRadioButton.value))
                                }
                                POP -> {
                                    backStack.pop()
                                }
                                REPLACE -> {
                                    backStack.replace(Child(selectedRadioButton.value))
                                }
                                REMOVE -> {
                                    backStack.remove(
                                        BackStack.LocalRoutingKey(
                                            Child(selectedRadioButton.value),
                                            typedId.value.toInt()
                                        )
                                    )
                                }
                                NEW_ROOT -> {
                                    backStack.newRoot(Child(selectedRadioButton.value))
                                }
                                SINGLE_TOP -> {
                                    backStack.singleTop(Child(selectedRadioButton.value))
                                }
                            }
                        }
                    ) {
                        Text(text = "Perform")
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "BackStack:", fontWeight = Bold)
                        Text(text = "${backStackState.value.toStateString()}")
                    }
                }
            }
        }
    }

    private fun BackStackElements<Routing>.toStateString() = map { element ->
        (element.key as BackStack.LocalRoutingKey).let { key ->
            val name = (key.routing as Child).name
            val uuid = key.uuid
            "$name(id: $uuid)"
        }
    }

    enum class Operation(
        val label: String,
        val radioButtonNeeded: Boolean,
        val idNeeded: Boolean
    ) {
        PUSH("Push", true, false),
        POP("Pop", false, false),
        REPLACE("Replace", true, false),
        REMOVE("Remove", true, true),
        NEW_ROOT("New Root", true, false),
        SINGLE_TOP("Single Top", true, false)
    }
}
