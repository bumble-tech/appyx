package com.github.zsoltk.composeribs.core

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.routing.Resolver
import com.github.zsoltk.composeribs.core.routing.RoutingKey

abstract class InnerNode<T> : Resolver<T> {

    // FIXME with Scope
    var children: List<ViewChildEntry<*>> = listOf()

    @Composable
    fun Compose(children: List<ViewChildEntry<*>>) {
        // FIXME with Scope
        this.children = children
        Compose()
    }

    @Composable
    abstract fun Compose()

    @Composable
    inline fun <reified V : T> placeholder(
        modifier: (RoutingKey<T>) -> Modifier = { Modifier },
        filter: (RoutingKey<out V>) -> Boolean = { true },
    ) {
        val filtered = remember(children, V::class.java) {
            children.filter {
                (it.key.routing is V || it.key.routing!!::class.java.isAssignableFrom(V::class.java)) &&
                    filter.invoke(it.key as RoutingKey<V>)
            }
        }

        filtered.forEach { child ->
            key(child.key) {
                val clientModifier = modifier(child.key as RoutingKey<T>)
                Box(modifier = clientModifier.then(child.modifier)) {
                    child.composable.Compose()
                }
            }
        }
    }
}
