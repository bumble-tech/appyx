package com.github.zsoltk.composeribs.core.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.routing.FallbackUpNavigationHandler
import com.github.zsoltk.composeribs.core.routing.LocalFallbackUpNavigationHandler

fun interface NodeFactory<N : Node> {
    fun create(buildContext: BuildContext): N
}

/**
 * Composable function to host [Node].
 *
 * Aligns lifecycle and manages state restoration.
 */
@Composable
fun <N : Node> NodeHost(
    upNavigationHandler: FallbackUpNavigationHandler,
    factory: NodeFactory<N>
) {
    CompositionLocalProvider(
        LocalFallbackUpNavigationHandler provides upNavigationHandler
    ) {
        val node by rememberNode(factory)
        node.Compose()
        DisposableEffect(node) {
            onDispose { node.updateLifecycleState(Lifecycle.State.DESTROYED) }
        }
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle) {
            node.updateLifecycleState(lifecycle.currentState)
            val observer = LifecycleEventObserver { source, _ ->
                node.updateLifecycleState(source.lifecycle.currentState)
            }
            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }
    }
}

@Composable
fun <N : Node> rememberNode(factory: NodeFactory<N>): State<N> =
    rememberSaveable(
        inputs = arrayOf(),
        stateSaver = mapSaver(
            save = { node -> node.onSaveInstanceState(this) },
            restore = { state -> factory.create(buildContext = BuildContext.root(state)) },
        ),
    ) {
        mutableStateOf(factory.create(buildContext = BuildContext.root(null)))
    }
