package com.github.zsoltk.composeribs.core

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

abstract class RibView<T> {

    // FIXME with Scope
    var children: Map<RoutingKey<T>, ViewChildEntry<*>> = mapOf()

    @Composable
    fun Compose(children: Map<RoutingKey<T>, ViewChildEntry<*>>) {
        // FIXME with Scope
        this.children = children
        Compose()
    }

    @Composable
    abstract fun Compose()

    @Composable
    inline fun <reified V : T> placeholder() {
        val filtered = remember(children, V::class.java) {
            children.filter { it.key.routing is V || it.key.routing!!::class.java.isAssignableFrom(V::class.java) }
        }

        filtered.values.forEach { child ->
            key(child.key) {
                Box(modifier = child.modifier) {
                    child.view.Compose()
                }
            }
        }
    }
}
