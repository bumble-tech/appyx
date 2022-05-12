package com.bumble.appyx.v2.sandbox.client.list

import android.os.Parcelable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.core.composable.Child
import com.bumble.appyx.v2.core.composable.visibleChildrenAsState
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.ParentNode
import com.bumble.appyx.v2.core.routing.RoutingElement
import com.bumble.appyx.v2.core.routing.source.permanent.PermanentRoutingSource
import com.bumble.appyx.v2.sandbox.client.child.ChildNode
import com.bumble.appyx.v2.sandbox.client.list.LazyListContainerNode.ListMode.Column
import com.bumble.appyx.v2.sandbox.client.list.LazyListContainerNode.ListMode.Grid
import com.bumble.appyx.v2.sandbox.client.list.LazyListContainerNode.ListMode.Row
import com.bumble.appyx.v2.sandbox.client.list.LazyListContainerNode.ListMode.values
import com.bumble.appyx.v2.sandbox.client.list.LazyListContainerNode.Routing
import kotlinx.parcelize.Parcelize

class LazyListContainerNode constructor(
    buildContext: BuildContext,
    routingSource: PermanentRoutingSource<Routing> = PermanentRoutingSource(
        routings = buildSet<Routing> {
            repeat(100) {
                add(Routing(it.toString()))
            }
        },
        savedStateMap = buildContext.savedStateMap
    )
) : ParentNode<Routing>(routingSource, buildContext) {
    @Parcelize
    data class Routing(val name: String) : Parcelable

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        ChildNode(routing.name, buildContext)

    enum class ListMode {
        Column, Row, Grid
    }


    @Composable
    override fun View(modifier: Modifier) {
        var selectedMode by remember { mutableStateOf(Column) }

        Column(modifier = Modifier) {
            Column {
                values().forEach { mode ->
                    RadioItem(mode, mode == selectedMode) { selectedMode = mode }
                }
            }

            val children by routingSource.visibleChildrenAsState()
            when (selectedMode) {
                Column -> ColumnExample(children)
                Row -> RowExample(children)
                Grid -> GridExample(children)
            }
        }
    }

    @Composable
    private fun ColumnExample(elements: List<RoutingElement<Routing, out Any?>>) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            items(elements, key = { element -> element.key.id }) { element ->
                Child(routingElement = element)
            }
        }
    }

    @Composable
    private fun RowExample(elements: List<RoutingElement<Routing, out Any?>>) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(elements, key = { element -> element.key.id }) { element ->
                Child(routingElement = element)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun GridExample(elements: List<RoutingElement<Routing, out Any?>>) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(elements) { element ->
                Child(routingElement = element)
            }
        }
    }


    @Composable
    private fun RadioItem(
        mode: LazyListContainerNode.ListMode,
        isSelected: Boolean,
        onClick: () -> Unit,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = isSelected,
                    onClick = onClick
                )
                .padding(horizontal = 16.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Text(
                text = mode.name,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
