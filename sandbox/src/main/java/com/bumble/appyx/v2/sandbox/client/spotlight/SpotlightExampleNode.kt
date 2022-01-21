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
import com.bumble.appyx.v2.sandbox.client.child.ChildNode
import com.bumble.appyx.v2.sandbox.client.spotlight.SpotlightExampleNode.Routing.Child1
import com.bumble.appyx.v2.sandbox.client.spotlight.SpotlightExampleNode.Routing.Child2
import com.bumble.appyx.v2.sandbox.client.spotlight.SpotlightExampleNode.Routing.Child3
import com.bumble.appyx.v2.core.composable.Subtree
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.source.spotlight.Spotlight
import com.bumble.appyx.v2.core.routing.source.spotlight.hasNext
import com.bumble.appyx.v2.core.routing.source.spotlight.hasPrevious
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.activate
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.next
import com.bumble.appyx.v2.core.routing.source.spotlight.operations.previous
import com.bumble.appyx.v2.core.routing.source.spotlight.transitionhandlers.rememberSpotlightSlider
import kotlinx.parcelize.Parcelize

class SpotlightExampleNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<Routing, ChildKeys> = Spotlight(
        items = getItems(),
        savedStateMap = buildContext.savedStateMap,
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
    enum class ChildKeys : Parcelable {
        C1, C2, C3
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
                        spotlight.activate(ChildKeys.C1)
                    }
                    TextButton(
                        text = "C2",
                        enabled = true
                    ) {
                        spotlight.activate(ChildKeys.C2)
                    }
                    TextButton(
                        text = "C3",
                        enabled = true
                    ) {
                        spotlight.activate(ChildKeys.C3)
                    }
                }

                Subtree(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 12.dp)
                        .fillMaxWidth(),
                    transitionHandler = rememberSpotlightSlider(clipToBounds = true),
                    routingSource = spotlight
                ) {
                    children<Routing> { child ->
                        child()
                    }
                }

            }
        }
    }

    @Composable
    private fun TextButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
        Button(onClick = onClick, enabled = enabled, modifier = Modifier.padding(4.dp)) {
            Text(text = text)
        }
    }

    companion object {
        private fun getItems() = mapOf(
            ChildKeys.C1 to Child1,
            ChildKeys.C2 to Child2,
            ChildKeys.C3 to Child3,
        )
    }
}
