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
import com.github.zsoltk.composeribs.client.backstack.BackStackExampleNode.Routing.*
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.ParentNode
import com.github.zsoltk.composeribs.core.Subtree
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElements
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackSlider
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.*
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

class BackStackExampleNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = ChildA(value = DEFAULT_VALUE),
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<Routing>(
    routingSource = backStack,
    buildContext = buildContext,
) {

    sealed class Routing(
        val name: String
    ) : Parcelable {

        abstract val value: String

        @Parcelize
        data class ChildA(
            override val value: String
        ) : Routing("A")

        @Parcelize
        data class ChildB(
            override val value: String
        ) : Routing("B")

        @Parcelize
        data class ChildC(
            override val value: String
        ) : Routing("C")

        @Parcelize
        data class ChildD(
            override val value: String
        ) : Routing("D")
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is ChildA -> ChildNode(routing.name, buildContext)
            is ChildB -> ChildNode(routing.name, buildContext)
            is ChildC -> ChildNode(routing.name, buildContext)
            is ChildD -> ChildNode(routing.name, buildContext)
        }

    @Composable
    override fun View() {
        val backStackState = backStack.all.collectAsState()
        val selectedChildRadioButton = rememberSaveable { mutableStateOf("") }
        val defaultOrRandomRadioButton = rememberSaveable { mutableStateOf(DEFAULT_LABEL) }
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
                    Subtree(
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 12.dp)
                            .fillMaxWidth(),
                        routingSource = backStack,
                        transitionHandler = BackStackSlider(clipToBounds = true)
                    ) {
                        children<Routing> { child ->
                            child()
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
                                        selected = it == selectedChildRadioButton.value,
                                        enabled = isRadioButtonNeeded.value,
                                        onClick = { selectedChildRadioButton.value = it }
                                    )
                                    Text(text = it)
                                    Spacer(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                        Row {
                            RadioButton(
                                selected = defaultOrRandomRadioButton.value == DEFAULT_LABEL,
                                enabled = isRadioButtonNeeded.value,
                                onClick = { defaultOrRandomRadioButton.value = DEFAULT_LABEL }
                            )
                            Text(text = DEFAULT_LABEL)
                            Spacer(modifier = Modifier.size(36.dp))
                            RadioButton(
                                selected = defaultOrRandomRadioButton.value == RANDOM_LABEL,
                                enabled = isRadioButtonNeeded.value,
                                onClick = { defaultOrRandomRadioButton.value = RANDOM_LABEL }
                            )
                            Text(text = RANDOM_LABEL)
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
                                        selectedChildRadioButton.value = ""
                                        defaultOrRandomRadioButton.value = DEFAULT_LABEL
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
                        if (selectedOperation.value?.radioButtonNeeded == true && selectedChildRadioButton.value.isEmpty()) {
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
                                    backStack.push(selectedChildRadioButton.value.toChild(random = defaultOrRandomRadioButton.value.random))
                                }
                                POP -> {
                                    backStack.pop()
                                }
                                REPLACE -> {
                                    backStack.replace(selectedChildRadioButton.value.toChild(random = defaultOrRandomRadioButton.value.random))
                                }
                                REMOVE -> {
                                    backStack.remove(
                                        BackStack.LocalRoutingKey(
                                            selectedChildRadioButton.value.toChild(random = defaultOrRandomRadioButton.value.random),
                                            typedId.value.toInt()
                                        )
                                    )
                                }
                                NEW_ROOT -> {
                                    backStack.newRoot(selectedChildRadioButton.value.toChild(random = defaultOrRandomRadioButton.value.random))
                                }
                                SINGLE_TOP -> {
                                    backStack.singleTop(
                                        selectedChildRadioButton.value.toChild(
                                            random = defaultOrRandomRadioButton.value.random
                                        )
                                    )
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
            val name = key.routing.name
            val uuid = key.uuid
            "$name(id: $uuid)"
        }
    }

    private fun String.toChild(random: Boolean): Routing {
        val value = if (random) Random.nextInt().toString() else DEFAULT_VALUE
        return when (this) {
            "A" -> ChildA(value = value)
            "B" -> ChildB(value = value)
            "C" -> ChildC(value = value)
            "D" -> ChildD(value = value)
            else -> throw IllegalArgumentException("Could not find the corresponding child!")
        }
    }

    private val String.random
        get() = this == RANDOM_LABEL

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

    companion object {

        private const val DEFAULT_LABEL = "Default"
        private const val RANDOM_LABEL = "Random"
        private const val DEFAULT_VALUE = "DEFAULT"
    }
}
