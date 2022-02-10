package com.bumble.appyx.v2.sandbox.interop

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.badoo.ribs.compose.ComposeRibView
import com.badoo.ribs.compose.ComposeView
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.v2.core.integration.NodeFactory
import com.bumble.appyx.v2.core.integration.NodeHost
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.v2.core.node.Node

class InteropNode<N : Node>(
    buildParams: BuildParams<*>,
    nodeFactory: NodeFactory<N>
) : com.badoo.ribs.core.Node<InteropRibView<N>>(
    buildParams = buildParams,
    viewFactory = InteropRibView.Factory<N>().invoke(object : InteropRibView.Dependencies<N> {
        override val nodeFactory: NodeFactory<N> = nodeFactory
    })
)

class InteropRibView<N : Node> private constructor(
    override val context: Context,
    private val nodeFactory: NodeFactory<N>,
) : ComposeRibView(context) {

    override val composable: ComposeView
        get() = @Composable {
            val integrationPoint = remember { retrieveIntegrationPoint() }
            NodeHost(integrationPoint, nodeFactory)
        }

    class Factory<N : Node> : ViewFactoryBuilder<Dependencies<N>, InteropRibView<N>> {
        override fun invoke(deps: Dependencies<N>): ViewFactory<InteropRibView<N>> =
            ViewFactory {
                InteropRibView(
                    context = it.parent.context,
                    nodeFactory = deps.nodeFactory
                )
            }
    }

    private fun retrieveIntegrationPoint(): IntegrationPoint {
        return if (context is RibInteropActivity) {
            context.integrationPointV2
        } else {
            IntegrationPointStub()
        }
    }

    interface Dependencies<N : Node> {
        val nodeFactory: NodeFactory<N>
    }
}
