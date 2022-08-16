package com.bumble.appyx.core.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.build

/**
 * Composable function to host [Node].
 *
 * Aligns lifecycle and manages state restoration.
 */
@Composable
fun <N : Node> NodeHost(
    customisations: NodeCustomisationDirectory = NodeCustomisationDirectoryImpl(),
    integrationPoint: IntegrationPoint,
    factory: NodeFactory<N>
) {
    val node by rememberNode(factory, customisations)
    DisposableEffect(integrationPoint) {
        integrationPoint.attach(node)
        onDispose { integrationPoint.detach() }
    }
    DisposableEffect(node) {
        onDispose { node.updateLifecycleState(Lifecycle.State.DESTROYED) }
    }
    node.Compose()
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

@Composable
internal fun <N : Node> rememberNode(
    factory: NodeFactory<N>,
    customisations: NodeCustomisationDirectory
): State<N> =
    rememberSaveable(
        inputs = arrayOf(),
        stateSaver = mapSaver(
            save = { node -> node.saveInstanceState(this) },
            restore = { state ->
                factory.create(
                    buildContext = BuildContext.root(
                        savedStateMap = state,
                        customisations = customisations
                    ),
                ).build()
            },
        ),
    ) {
        mutableStateOf(
            factory.create(
                buildContext = BuildContext.root(
                    savedStateMap = null,
                    customisations = customisations
                )
            ).build()
        )
    }
