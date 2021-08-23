package com.github.zsoltk.composeribs.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.core.routing.impl.backstack.BackStack

abstract class RibView<T> {

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
    inline fun <reified V : T> placeholder() {
        val filtered = remember(children, V::class.java) {
            children.filter { it.key.routing is V || it.key.routing!!::class.java.isAssignableFrom(V::class.java) }
        }

        filtered.forEach { child ->
            key(child.key) {
                Row(modifier = Modifier.padding(bottom = 12.dp)) {
                    Text(text = (child.key as BackStack.LocalRoutingKey).uuid.toString())
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = child.modifier) {
                        child.view.Compose()
                    }
                }
            }
        }
    }
}
