package com.bumble.appyx.interop.v1v2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.badoo.ribs.compose.ComposeRibView
import com.badoo.ribs.compose.ComposeView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.v2.core.integration.NodeFactory
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.node.build
import com.bumble.appyx.v2.core.routing.upnavigation.LocalFallbackUpNavigationHandler

class V1V2View<N : Node> private constructor(
    override val context: Context,
    private val nodeFactory: NodeFactory<N>,
) : ComposeRibView(context) {

    private var content by mutableStateOf<ComposeView?>(null)

    override val composable: ComposeView
        get() = @Composable {
            content?.invoke()
        }

    fun initialise(v2Node: N?, nodeUpdateListener: NodeUpdateListener<N>) {
        content = @Composable {
            val integrationPoint = remember { retrieveIntegrationPoint() }
            CompositionLocalProvider(LocalFallbackUpNavigationHandler provides integrationPoint) {
                val node by rememberNode(node = v2Node, factory = nodeFactory)
                LaunchedEffect(key1 = node) {
                    node.integrationPoint = integrationPoint
                    nodeUpdateListener.onNodeUpdated(node)
                }
                node.Compose()
            }
        }
    }

    class Factory<N : Node> : ViewFactoryBuilder<Dependencies<N>, V1V2View<N>> {
        override fun invoke(deps: Dependencies<N>): ViewFactory<V1V2View<N>> =
            ViewFactory {
                V1V2View(
                    context = it.parent.context,
                    nodeFactory = deps.nodeFactory,
                )
            }
    }

    private fun retrieveIntegrationPoint(): IntegrationPoint {
        val activity = context.findActivity<AppCompatActivity>()
        return if (activity is IntegrationPointV2Provider) {
            activity.integrationPointV2
        } else {
            throw IllegalStateException("Activity where V1V2 is used must implement IntegrationPointV2Provider")
        }
    }

    interface Dependencies<N : Node> {
        val nodeFactory: NodeFactory<N>
    }
}

@Composable
private fun <N : Node> rememberNode(
    node: N?,
    factory: NodeFactory<N>
): State<N> =
    rememberSaveable(
        inputs = arrayOf(),
        stateSaver = mapSaver(
            save = { node -> node.onSaveInstanceState(this) },
            restore = { state -> factory.create(buildContext = BuildContext.root(state)).build() },
        ),
    ) {
        mutableStateOf(node ?: factory.create(buildContext = BuildContext.root(null)).build())
    }
