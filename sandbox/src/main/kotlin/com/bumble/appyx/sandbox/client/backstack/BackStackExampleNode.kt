package com.bumble.appyx.sandbox.client.backstack

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.operation.newRoot
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.remove
import com.bumble.appyx.navmodel.backstack.operation.replace
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Operation.NEW_ROOT
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Operation.POP
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Operation.PUSH
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Operation.REMOVE
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Operation.REPLACE
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Operation.SINGLE_TOP
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Operation.values
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Routing
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Routing.ChildA
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Routing.ChildB
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Routing.ChildC
import com.bumble.appyx.sandbox.client.backstack.BackStackExampleNode.Routing.ChildD
import com.bumble.appyx.sandbox.client.child.ChildNode
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

class BackStackExampleNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = ChildA(value = DEFAULT_VALUE),
        savedStateMap = buildContext.savedStateMap,
        backPressHandler = DontHandleBackPress(),
    )
) : ParentNode<Routing>(
    navModel = backStack,
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
    override fun View(modifier: Modifier) {
        val backStackState = backStack.elements.collectAsState()
        val selectedChildRadioButton = rememberSaveable { mutableStateOf("") }
        val defaultOrRandomRadioButton = rememberSaveable { mutableStateOf(DEFAULT_LABEL) }
        val isRadioButtonNeeded = rememberSaveable { mutableStateOf(false) }
        val selectedId = rememberSaveable { mutableStateOf("") }
        val isIdNeeded = rememberSaveable { mutableStateOf(false) }
        val selectedOperation = rememberSaveable { mutableStateOf<Operation?>(null) }
        val areThereMissingParams = rememberSaveable { mutableStateOf(true) }
        val skipChildRenderingByRouting = rememberSaveable { mutableStateOf(NONE_VALUE) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text("Back stack example placeholder")
            Column(
                Modifier.padding(24.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Children(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 12.dp)
                        .fillMaxWidth(),
                    navModel = backStack,
                    transitionHandler = rememberBackStackExampleTransitionHandler()
                ) {
                    children<Routing> { child, descriptor ->
                        if (!descriptor.element.isFiltered(skipChildRenderingByRouting.value)) {
                            child()
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
                    val expanded = rememberSaveable { mutableStateOf(false) }
                    Text(
                        text = if (selectedId.value.isNotEmpty()) selectedId.value else "Select an id",
                        modifier = if (isIdNeeded.value) {
                            Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .clickable { expanded.value = true }
                        } else {
                            Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray)
                        }
                    )
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        backStackState.value.iterator().forEach { element ->
                            DropdownMenuItem(onClick = {
                                selectedId.value = element.key.id
                                expanded.value = false
                            }) {
                                Text(text = element.key.id)
                            }
                        }
                    }
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
                                    selectedId.value = ""
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
                    if (selectedOperation.value?.idNeeded == true && selectedId.value.isEmpty()) {
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
                                backStack.remove(backStackState.value.first { it.key.id == selectedId.value }.key)
                                selectedId.value = ""
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
                            null -> Unit
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
                Spacer(modifier = Modifier.size(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Skip rendering of:", fontWeight = Bold)
                    Spacer(modifier = Modifier.size(8.dp))
                    Row {
                        listOf(NONE_VALUE, "A", "B", "C", "D").forEach {
                            Row {
                                RadioButton(
                                    selected = it == skipChildRenderingByRouting.value,
                                    onClick = { skipChildRenderingByRouting.value = it }
                                )
                                Text(text = it)
                                Spacer(modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun BackStackElements<Routing>.toStateString() = map { element ->
        val key = element.key
        val name = key.routing.name
        val value = key.routing.value
        val id = key.id
        "$name(Value: $value. Id: $id)"
    }

    private fun String.toChild(random: Boolean): Routing {
        val value = if (random) Random.nextInt(1000).toString() else DEFAULT_VALUE
        return when (this) {
            "A" -> ChildA(value = value)
            "B" -> ChildB(value = value)
            "C" -> ChildC(value = value)
            "D" -> ChildD(value = value)
            else -> throw IllegalArgumentException("Could not find the corresponding child!")
        }
    }

    private fun Routing.isFiltered(filter: String) = when (filter) {
        "A" -> this is ChildA
        "B" -> this is ChildB
        "C" -> this is ChildC
        "D" -> this is ChildD
        else -> false
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
        REMOVE("Remove", false, true),
        NEW_ROOT("New Root", true, false),
        SINGLE_TOP("Single Top", true, false)
    }

    companion object {
        private const val DEFAULT_LABEL = "Default"
        private const val RANDOM_LABEL = "Random"
        private const val DEFAULT_VALUE = "DEFAULT"
        private const val NONE_VALUE = "NONE"
    }
}
