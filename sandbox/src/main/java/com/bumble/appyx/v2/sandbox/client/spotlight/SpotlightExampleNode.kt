package com.bumble.appyx.v2.sandbox.client.spotlight

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.core.composable.Children
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.operationstrategies.QueueOperations
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.v2.core.routing.source.spotlight.backpresshandler.GoToPrevious
import com.bumble.appyx.v2.core.routing.source.spotlight.hasNext
import com.bumble.appyx.v2.core.routing.source.spotlight.hasPrevious
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.activate
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.next
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.previous
import com.bumble.appyx.v2.core.routing.source.spotlight.transitionhandler.rememberSpotlightSlider
import com.bumble.appyx.v2.sandbox.client.child.ChildNode
import com.bumble.appyx.v2.sandbox.client.spotlight.SpotlightExampleNode.Item.C1
import com.bumble.appyx.v2.sandbox.client.spotlight.SpotlightExampleNode.Item.C2
import com.bumble.appyx.v2.sandbox.client.spotlight.SpotlightExampleNode.Item.C3
import com.bumble.appyx.v2.sandbox.client.spotlight.SpotlightExampleNode.Routing.Child1
import com.bumble.appyx.v2.sandbox.client.spotlight.SpotlightExampleNode.Routing.Child2
import com.bumble.appyx.v2.sandbox.client.spotlight.SpotlightExampleNode.Routing.Child3
import kotlinx.parcelize.Parcelize

class SpotlightExampleNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<Routing> = Spotlight(
        items = Item.getItemList(),
        savedStateMap = buildContext.savedStateMap,
        backPressHandler = GoToPrevious(),
        operationStrategy = QueueOperations()
    )
) : ParentNode<SpotlightExampleNode.Routing>(
    buildContext = buildContext,
    routingSource = spotlight
) {

    sealed class Routing : Parcelable {

        @Parcelize
        object Child1 : Routing()

        @Parcelize
        object Child2 : Routing()

        @Parcelize
        object Child3 : Routing()
    }

    @Parcelize
    private enum class Item(val routing: Routing) : Parcelable {
        C1(Child1),
        C2(Child2),
        C3(Child3);

        companion object {
            fun getItemList() = values().map { it.routing }
        }
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            Child1 -> ChildNode(name = "Child1", buildContext = buildContext)
            Child2 -> ChildNode(name = "Child2", buildContext = buildContext)
            Child3 -> ChildNode(name = "Child3", buildContext = buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        val hasPrevious = spotlight.hasPrevious().collectAsState(initial = false)
        val hasNext = spotlight.hasNext().collectAsState(initial = false)

        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        text = "Previous",
                        enabled = hasPrevious.value
                    ) {
                        spotlight.previous()
                    }
                    TextButton(
                        text = "Next",
                        enabled = hasNext.value
                    ) {
                        spotlight.next()
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        text = "C1",
                        enabled = true
                    ) {
                        spotlight.activate(C1)
                    }
                    TextButton(
                        text = "C2",
                        enabled = true
                    ) {
                        spotlight.activate(C2)
                    }
                    TextButton(
                        text = "C3",
                        enabled = true
                    ) {
                        spotlight.activate(C3)
                    }
                }

                Children(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 12.dp)
                        .fillMaxWidth(),
                    transitionHandler = rememberSpotlightSlider(clipToBounds = true),
                    routingSource = spotlight
                )

            }
        }
    }

    @Composable
    private fun TextButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
        Button(onClick = onClick, enabled = enabled, modifier = Modifier.padding(4.dp)) {
            Text(text = text)
        }
    }

    private fun Spotlight<*>.activate(item: Item) {
        activate(item.ordinal)
    }
}
