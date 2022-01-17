package com.github.zsoltk.composeribs.client.interactorusage

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.interactorusage.InteractorNode.Routing
import com.github.zsoltk.composeribs.core.clienthelper.interactor.Interactor
import com.github.zsoltk.composeribs.core.composable.Subtree
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.node.node
import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.pop
import com.github.zsoltk.composeribs.core.routing.source.backstack.operation.push
import com.github.zsoltk.composeribs.core.routing.source.backstack.rememberBackstackFader
import com.github.zsoltk.composeribs.core.routing.source.backstack.rememberBackstackSlider
import com.github.zsoltk.composeribs.core.routing.transition.rememberCombinedHandler
import kotlinx.parcelize.Parcelize

class InteractorNode(
    interactor: Interactor,
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Routing.Child1,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<Routing>(
    routingSource = backStack,
    buildContext = buildContext,
    plugins = listOf(interactor)
), UpNavigationHandler {

    var child2InfoState by mutableStateOf("Here will appear child2 info")
    var child3InfoState by mutableStateOf("Here will appear child3 info")

    sealed class Routing : Parcelable {
        @Parcelize
        object Child1 : Routing()

        @Parcelize
        object Child2 : Routing()

        @Parcelize
        object Child3 : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Child1 -> node(buildContext) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.LightGray)
                )
            }
            is Routing.Child2 -> Child2Node(buildContext)
            is Routing.Child3 -> Child3Node(buildContext)
        }

    @Composable
    override fun View() {
        Column(modifier = Modifier.fillMaxSize()) {
            Subtree(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .requiredHeight(250.dp),
                routingSource = backStack,
                transitionHandler = rememberCombinedHandler(
                    handlers = listOf(rememberBackstackSlider(), rememberBackstackFader())
                )
            ) {
                children<Routing> { child ->
                    child()
                }
            }
            Spacer(modifier = Modifier.requiredHeight(8.dp))

            Button(
                onClick = { backStack.push(Routing.Child2) },
                modifier = Modifier.padding(4.dp),
            ) {
                Text(text = "Push Child2")
            }

            Spacer(modifier = Modifier.requiredHeight(8.dp))

            Button(
                onClick = { backStack.push(Routing.Child3) },
                modifier = Modifier.padding(4.dp),
            ) {
                Text(text = "Push Child3")
            }

            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Text(text = "Child2 info :")
            Text(text = child2InfoState)

            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Text(text = "Child3 info :")

            Text(text = child3InfoState)
        }
    }

    override fun handleUpNavigation(): Boolean {
        backStack.pop()
        return true
    }
}
