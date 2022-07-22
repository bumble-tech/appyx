package com.bumble.appyx.sandbox.client.workflow

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.operation.push
import com.bumble.appyx.sandbox.client.workflow.ChildNodeB.Routing
import com.bumble.appyx.sandbox.client.workflow.ChildNodeB.Routing.ChildA
import com.bumble.appyx.sandbox.client.workflow.ChildNodeB.Routing.ChildB
import kotlinx.parcelize.Parcelize

class ChildNodeB(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = ChildA,
        savedStateMap = buildContext.savedStateMap
    ),
) : ParentNode<Routing>(
    buildContext = buildContext,
    routingSource = backStack,
) {

    sealed class Routing : Parcelable {
        @Parcelize
        object ChildA : Routing()

        @Parcelize
        object ChildB : Routing()
    }

    suspend fun attachChildBCoroutine(): GrandChildNodeB {
        return attachWorkflow {
            backStack.push(ChildB)
        }
    }

    override fun resolve(routing: Routing, buildContext: BuildContext) =
        when (routing) {
            is ChildA -> GrandChildNodeA(buildContext)
            is ChildB -> GrandChildNodeB(buildContext)
        }


    @Composable
    override fun View(modifier: Modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.LightGray)
        ) {
            Text(
                text = "Child two"
            )
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Children(routingSource = backStack)
        }
    }

    class GrandChildNodeA(buildContext: BuildContext) : Node(buildContext) {

        @Composable
        override fun View(modifier: Modifier) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = Color.LightGray)
            ) {
                Text(
                    text = "Grandchild one",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    class GrandChildNodeB(buildContext: BuildContext) : Node(buildContext) {

        suspend fun printLifecycleEvent(): GrandChildNodeB {
            return executeWorkflow {
                Log.e("Lifecycle", lifecycle.currentState.toString())
            }
        }

        @Composable
        override fun View(modifier: Modifier) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(color = Color.LightGray)
            ) {
                Text(
                    text = "Grandchild two",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
