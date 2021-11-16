package com.github.zsoltk.composeribs.client.combined

import android.os.Parcelable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.child.ChildNode
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.composable.Subtree
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.push
import com.github.zsoltk.composeribs.core.routing.source.backstack.rememberBackstackFader
import com.github.zsoltk.composeribs.core.routing.source.combined.plus
import kotlinx.parcelize.Parcelize
import java.util.UUID

class CombinedRoutingSourceNode(
    buildContext: BuildContext,
    private val backStack1: BackStack<Routing> = BackStack(
        initialElement = Routing.Configuration.Child(UUID.randomUUID().toString()),
        savedStateMap = buildContext.savedStateMap,
        key = "BackStack1",
    ),
    private val backStack2: BackStack<Routing> = BackStack(
        initialElement = Routing.Configuration.Child(UUID.randomUUID().toString()),
        savedStateMap = buildContext.savedStateMap,
        key = "BackStack2",
    ),
) : ParentNode<CombinedRoutingSourceNode.Routing>(
    buildContext = buildContext,
    routingSource = backStack1 + backStack2,
) {

    sealed class Routing : Parcelable {
        sealed class Permanent : Routing() {
            @Parcelize
            object Child1 : Permanent()
        }

        sealed class Configuration : Routing() {
            @Parcelize
            data class Child(val id: String) : Configuration()
        }
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Configuration.Child -> ChildNode(routing.id, buildContext)
            is Routing.Permanent.Child1 -> ChildNode("Permanent", buildContext)
        }

    @Composable
    override fun View() {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {

            Permanent()

            Spacer(modifier = Modifier.height(16.dp))

            BackStack("BackStack1", backStack1)

            Spacer(modifier = Modifier.height(16.dp))

            BackStack("BackStack2", backStack2)

        }
    }

    @Composable
    private fun Permanent() {
        Text(text = "Permanent")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            permanentChild(Routing.Permanent.Child1)
        }
    }

    @Composable
    private fun BackStack(
        name: String,
        backStack: BackStack<Routing>,
    ) {
        Text(text = name)
        Subtree(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            routingSource = backStack,
            transitionHandler = rememberBackstackFader(transitionSpec = { tween(300) }),
        ) {
            children<Routing> { child ->
                child()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                backStack.push(
                    Routing.Configuration.Child(
                        UUID.randomUUID().toString()
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Add", modifier = Modifier.padding(horizontal = 16.dp))
        }
    }

}
