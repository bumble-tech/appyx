package com.github.zsoltk.composeribs.core.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.github.zsoltk.composeribs.core.children.ChildEntry
import com.github.zsoltk.composeribs.core.node.LocalNode
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import kotlinx.coroutines.flow.map

@Composable
fun <T : Any, S> BasicSubtree(
    routingSource: RoutingSource<T, S>,
    block: @Composable BasicSubtreeScope<T, S>.() -> Unit
) {
    block(BasicSubtreeScope(routingSource))
}

class BasicSubtreeScope<T : Any, S>(
    val routingSource: RoutingSource<T, S>,
) {

    @Composable
    inline fun <reified V : T> visibleChildren(): State<List<ChildEntry.Eager<T>>> {
        val node = LocalNode.current?.let { it as? ParentNode<T> }
            ?: error("Subtree can't be invoked outside of a ParentNode's Composable context. LocalNode.current is ${LocalNode.current}")
        return this@BasicSubtreeScope.routingSource.onScreen
            .map { list ->
                list
                    .filter { it.key.routing is V }
                    .map { node.childOrCreate(it.key) }
            }.collectAsState(initial = emptyList())
    }

    @Composable
    inline fun <reified V : T> visibleChildren(
        block: @Composable (child: @Composable () -> Unit) -> Unit,
    ) {
        val children by visibleChildren<V>()

        children.forEach { childEntry ->
            key(childEntry.key) {
                block(
                    child = { childEntry.node.Compose() }
                )
            }
        }
    }
}
