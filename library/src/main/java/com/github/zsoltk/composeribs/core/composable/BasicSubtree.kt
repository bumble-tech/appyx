package com.github.zsoltk.composeribs.core.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.github.zsoltk.composeribs.core.node.LocalNode
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.RoutingElements
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
    inline fun <reified V : T> visibleChildren(): State<RoutingElements<T, out S>> {
        val node = LocalNode.current?.let { it as? ParentNode<T> }
            ?: error("Subtree can't be invoked outside of a ParentNode's Composable context. LocalNode.current is ${LocalNode.current}")
        return this@BasicSubtreeScope.routingSource.onScreen
            .map { list ->
                list
                    .filter { it.key.routing is V }
                    .forEach { node.childOrCreate(it.key) }

                list
            }.collectAsState(initial = emptyList())
    }
}
