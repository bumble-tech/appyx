package com.github.zsoltk.composeribs.client.spotlight

import android.os.Parcelable
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.spotlight.SpotlightExampleNode.Routing.Child1
import com.github.zsoltk.composeribs.client.spotlight.SpotlightExampleNode.Routing.Child2
import com.github.zsoltk.composeribs.client.spotlight.SpotlightExampleNode.Routing.Child3
import com.github.zsoltk.composeribs.core.composable.Subtree
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.node.node
import com.github.zsoltk.composeribs.core.routing.source.spotlight.Spotlight
import com.github.zsoltk.composeribs.core.routing.source.spotlight.SpotlightItem
import com.github.zsoltk.composeribs.core.routing.source.spotlight.SpotlightItems
import com.github.zsoltk.composeribs.core.routing.source.spotlight.hasNext
import com.github.zsoltk.composeribs.core.routing.source.spotlight.hasPrevious
import com.github.zsoltk.composeribs.core.routing.source.spotlight.operations.next
import com.github.zsoltk.composeribs.core.routing.source.spotlight.operations.previous
import com.github.zsoltk.composeribs.core.routing.source.spotlight.transitionhandlers.SpotlightFader
import com.github.zsoltk.composeribs.core.routing.source.spotlight.transitionhandlers.SpotlightSlider
import com.github.zsoltk.composeribs.core.routing.source.spotlight.transitionhandlers.rememberSpotlightSlider
import com.github.zsoltk.composeribs.core.routing.transition.rememberCombinedHandler
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
            Child1 -> node(buildContext) { Placeholder(name = "Child1", color = Color.LightGray) }
            Child2 -> node(buildContext) { Placeholder(name = "Child2", color = Color.Magenta) }
            Child3 -> node(buildContext) { Placeholder(name = "Child3", color = Color.Cyan) }
        }


    @Composable
    override fun View() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        text = "Previous",
                        enabled = spotlight.hasPrevious()
                    ) {
                        spotlight.previous()
                    }
                    TextButton(
                        text = "Next",
                        enabled = spotlight.hasNext()
                    ) {
                        spotlight.next()
                    }
                }
                Subtree(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 12.dp)
                        .fillMaxWidth(),
                   transitionHandler = rememberSpotlightSlider(clipToBounds = true),
//                    transitionHandler =rememberCombinedHandler(
//                        handlers = listOf(
//                            SpotlightFader(),
//                            SpotlightSlider(clipToBounds = true),
//                        )
//                    ),
                    adapter = spotlight.adapter
                ) {
                    children<Routing> { child ->
                        child()
                    }
                }
            }
        }
    }

    @Composable
    fun Placeholder(name: String, color: Color) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "This screen is a placeholder for $name")
            }
        }
    }

    @Composable
    private fun TextButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
        Button(onClick = onClick, enabled = enabled) {
            Text(text = text)
        }
    }

    companion object {
        private fun getItems() = SpotlightItems(
            items = listOf(
                SpotlightItem(
                    Child1,
                    ChildKeys.C1,
                ),
                SpotlightItem(
                    Child2,
                    ChildKeys.C2,
                ),
                SpotlightItem(
                    Child3,
                    ChildKeys.C3,
                ),
            )
        )
    }
}