package com.bumble.appyx.navigation.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.integrationpoint.IntegrationPoint
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.lifecycle.PlatformLifecycleEventObserver
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.build
import com.bumble.appyx.navigation.state.SavedStateMap
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl

data class ScreenSize(val widthDp: Dp, val heightDp: Dp)

val LocalScreenSize = compositionLocalOf { ScreenSize(0.dp, 0.dp) }

/**
 * Composable function to host [Node].
 *
 * Aligns lifecycle and manages state restoration.
 */
@Suppress("ComposableParamOrder") // detekt complains as 'factory' param isn't a pure lambda
@Composable
fun <N : Node> NodeHost(
    lifecycle: Lifecycle,
    integrationPoint: IntegrationPoint,
    screenSize: ScreenSize,
    modifier: Modifier = Modifier,
    customisations: NodeCustomisationDirectory = remember { NodeCustomisationDirectoryImpl() },
    factory: NodeFactory<N>
) {
    CompositionLocalProvider(LocalScreenSize provides screenSize) {
        val node by rememberNode(factory, customisations, integrationPoint)
        DisposableEffect(node) {
            onDispose { node.updateLifecycleState(Lifecycle.State.DESTROYED) }
        }
        node.Compose(modifier = modifier)
        DisposableEffect(lifecycle) {
            node.updateLifecycleState(lifecycle.currentState)
            val observer = PlatformLifecycleEventObserver { newState, _ ->
                node.updateLifecycleState(newState)
            }
            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }
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
