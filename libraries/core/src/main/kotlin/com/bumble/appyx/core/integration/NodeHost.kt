package com.bumble.appyx.core.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl
import com.bumble.appyx.viewmodel.IntegrationPointViewModel

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
        onDispose { node.updateLifecycleState(Lifecycle.State.DESTROYED) }
    }
    node.Compose(modifier = modifier)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val viewmodelStoreOwner = LocalViewModelStoreOwner.current
    val viewModel = IntegrationPointViewModel.getInstance(viewmodelStoreOwner!!)
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
