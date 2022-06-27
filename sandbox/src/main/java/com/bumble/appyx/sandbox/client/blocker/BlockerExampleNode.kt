package com.bumble.appyx.sandbox.client.blocker

import android.os.Parcelable
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.operation.push
import com.bumble.appyx.sandbox.client.child.ChildNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.Parcelize
import java.util.UUID

class BlockerExampleNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Child> = BackStack(
        savedStateMap = buildContext.savedStateMap,
        initialElement = Child(UUID.randomUUID().toString()),
    )
) : ParentNode<BlockerExampleNode.Child>(
    buildContext = buildContext,
    routingSource = backStack,
) {

    @Parcelize
    data class Child(val id: String) : Parcelable

    private val allowNavigateUpFromChildren = MutableStateFlow(true)

    override fun resolve(routing: Child, buildContext: BuildContext): Node =
        ChildNode(routing.id, buildContext)

    @Composable
    override fun View(modifier: Modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {

            val context = LocalContext.current
            var lastToastClickTime by remember { mutableStateOf(0L) }
            BackHandler {
                val time = System.currentTimeMillis()
                if (time - lastToastClickTime > 5000L) {
                    Toast.makeText(
                        context,
                        "It is a blocker, you can't go back by yourself, use 'go up'",
                        Toast.LENGTH_SHORT,
                    ).show()
                    lastToastClickTime = time
                }
            }

            Button(onClick = { backStack.push(Child(UUID.randomUUID().toString())) }) {
                Text(text = "Push child")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Allow navigate up events from children")
                val checked by allowNavigateUpFromChildren.collectAsState()
                Checkbox(
                    checked = checked,
                    onCheckedChange = { allowNavigateUpFromChildren.value = it },
                )
            }

            Children(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                routingSource = backStack,
            )
        }
    }

    override fun performUpNavigation(): Boolean =
        if (allowNavigateUpFromChildren.value) {
            super.performUpNavigation()
        } else {
            true
        }

}
