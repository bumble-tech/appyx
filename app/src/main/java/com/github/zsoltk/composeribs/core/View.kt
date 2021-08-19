package com.github.zsoltk.composeribs.core

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

abstract class RibView<T> {

    // FIXME with Scope
    var children: Map<RoutingKey<T>, RibView<*>> = mapOf()

    @Composable
    fun Compose(children: Map<RoutingKey<T>, RibView<*>>) {
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

        filtered.forEach {
            // TODO get modifier for transition
            Box(modifier = Modifier) {
                it.value.Compose()
            }
        }
    }
}
