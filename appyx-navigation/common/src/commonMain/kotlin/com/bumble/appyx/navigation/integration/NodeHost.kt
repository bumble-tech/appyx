package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.build
import com.bumble.appyx.navigation.platform.PlatformLifecycle
import com.bumble.appyx.navigation.platform.PlatformLifecycleEventObserver
import com.bumble.appyx.navigation.state.SavedStateMap
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl

/**
 * Composable function to host [Node].
 *
 * Aligns lifecycle and manages state restoration.
 */
@Suppress("ComposableParamOrder") // detekt complains as 'factory' param isn't a pure lambda
@Composable
fun <N : Node> NodeHost(
    integrationPoint: IntegrationPoint,
    modifier: Modifier = Modifier,
    customisations: NodeCustomisationDirectory = remember { NodeCustomisationDirectoryImpl() },
    factory: NodeFactory<N>
) {
    val node by rememberNode(factory, customisations, integrationPoint)
    DisposableEffect(node) {
        onDispose { node.updateLifecycleState(PlatformLifecycle.State.DESTROYED) }
    }
    node.Compose(modifier = modifier)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        node.updateLifecycleState(lifecycle.currentState)
        val observer = PlatformLifecycleEventObserver { newState, _ ->
            node.updateLifecycleState(newState)
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}

@Composable
internal fun <N : Node> rememberNode(
    factory: NodeFactory<N>,
    customisations: NodeCustomisationDirectory,
    integrationPoint: IntegrationPoint,
): State<N> {

    fun createNode(savedStateMap: SavedStateMap?): N =
        factory
            .create(
                buildContext = BuildContext.root(
                    savedStateMap = savedStateMap,
                    customisations = customisations
                ),
            )
            .apply { this.integrationPoint = integrationPoint }
            .build()

    return rememberSaveable(
        inputs = arrayOf(),
        stateSaver = mapSaver(
            save = { node -> node.saveInstanceState(this) },
            restore = { state -> createNode(savedStateMap = state) },
        ),
    ) {
        mutableStateOf(createNode(savedStateMap = null))
    }
}
