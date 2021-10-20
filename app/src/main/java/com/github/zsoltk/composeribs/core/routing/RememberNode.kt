package com.github.zsoltk.composeribs.core.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.github.zsoltk.composeribs.core.Node

fun interface NodeFactory<N : Node<*>> {
    fun create(restoreState: Map<String, Any>?): N
}

// TODO Expose inputs?
@Composable
fun <N : Node<*>> rememberNode(factory: NodeFactory<N>): State<N> =
    rememberSaveable(
        inputs = emptyArray(),
        stateSaver = mapSaver(
            save = { node -> node.onSaveInstanceState(this) },
            restore = { state -> factory.create(restoreState = state) },
        ),
    ) {
        mutableStateOf(factory.create(restoreState = null))
    }
