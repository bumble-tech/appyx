package com.bumble.appyx.sandbox.client.workflow

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.operation.pop
import com.bumble.appyx.routingsource.backstack.operation.push
import com.bumble.appyx.sandbox.client.workflow.RootNode.Routing
import com.bumble.appyx.sandbox.client.workflow.RootNode.Routing.ChildA
import com.bumble.appyx.sandbox.client.workflow.RootNode.Routing.ChildB
import kotlinx.parcelize.Parcelize

class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = ChildA,
        savedStateMap = buildContext.savedStateMap
    ),
    plugins: List<Plugin> = emptyList()
) : ParentNode<Routing>(
    buildContext = buildContext,
    routingSource = backStack,
    plugins = plugins
) {

    suspend fun waitForChildBAttached(): ChildNodeB {
        return waitForChildAttached()
    }

    suspend fun attachChildBCoroutine(): ChildNodeB {
        return attachWorkflow {
            backStack.push(ChildB)
        }
    }

    sealed class Routing : Parcelable {
        @Parcelize
        object ChildA : Routing()

        @Parcelize
        object ChildB : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext) =
        when (routing) {
            is ChildA -> ChildNodeA(buildContext)
            is ChildB -> ChildNodeB(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { backStack.push(ChildA) }) {
                    Text(text = "Push A")
                }
                Button(onClick = { backStack.push(ChildB) }) {
                    Text(text = "Push B")
                }
                Button(onClick = { backStack.pop() }) {
                    Text(text = "Pop")
                }
            }
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Children(routingSource = backStack)
        }
    }
}
