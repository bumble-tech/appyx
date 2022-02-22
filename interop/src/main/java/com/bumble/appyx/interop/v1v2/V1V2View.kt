package com.bumble.appyx.interop.v1v2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.badoo.ribs.compose.ComposeRibView
import com.badoo.ribs.compose.ComposeView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.v2.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.v2.core.node.Node

internal class V1V2View private constructor(
    override val context: Context,
    private val v2Node: Node,
) : ComposeRibView(context) {

    private val integrationPoint = retrieveIntegrationPoint()

    init {
        v2Node.integrationPoint = integrationPoint
    }

    override val composable: ComposeView
        get() = @Composable {
            v2Node.Compose()
        }

    class Factory<N: Node> : ViewFactoryBuilder<Dependency<N>, V1V2View> {
        override fun invoke(deps: Dependency<N>): ViewFactory<V1V2View> =
            ViewFactory {
                V1V2View(
                    context = it.parent.context,
                    v2Node = deps.v2Node
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

    interface Dependency<N : Node> {
        val v2Node: N
    }
}
