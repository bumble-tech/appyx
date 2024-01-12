package com.bumble.appyx.utils.interop.ribs

import android.os.Parcelable
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.Lifecycle
import com.badoo.ribs.builder.Builder
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.AndroidRibView2
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.resolution.ChildResolution
import com.badoo.ribs.routing.resolution.Resolution
import com.badoo.ribs.routing.router.Router
import com.badoo.ribs.routing.source.backstack.BackStack
import com.badoo.ribs.routing.source.backstack.operation.push
import com.bumble.appyx.navigation.integration.IntegrationPoint
import com.bumble.appyx.navigation.modality.NodeContext
import kotlinx.parcelize.Parcelize
import java.util.UUID

class RibsNode(
    buildParams: BuildParams<*>,
    private val backStack: BackStack<RibsNodeRouter.Configuration>,
    plugins: List<Plugin>,
) : Node<RibsNode.View>(
    buildParams = buildParams,
    viewFactory = View.Factory(),
    plugins = plugins,
) {

    fun current(): String =
        backStack.activeConfiguration.id

    fun push(): String {
        val id = UUID.randomUUID().toString()
        backStack.push(RibsNodeRouter.Configuration(id))
        return id
    }

    class View(
        androidView: ViewGroup,
        lifecycle: Lifecycle,
    ) : AndroidRibView2(
        androidView,
        lifecycle
    ) {
        class Factory : ViewFactory<View> {
            override fun invoke(context: ViewFactory.Context): View =
                View(FrameLayout(context.parent.context), context.lifecycle)
        }
    }
}

class RibsNodeBuilder : Builder<IntegrationPoint, RibsNode>() {
    override fun build(buildParams: BuildParams<IntegrationPoint>): RibsNode {
        val backStack = BackStack(
            RibsNodeRouter.Configuration(UUID.randomUUID().toString()),
            buildParams
        )
        val router = RibsNodeRouter(buildParams, buildParams.payload, backStack)
        return RibsNode(buildParams, backStack, listOf(router))
    }
}

class RibsNodeRouter(
    buildParams: BuildParams<*>,
    private val integrationPoint: IntegrationPoint,
    backStack: BackStack<Configuration>,
) : Router<RibsNodeRouter.Configuration>(
    buildParams = buildParams,
    routingSource = backStack
) {
    @Parcelize
    data class Configuration(val id: String) : Parcelable

    override fun resolve(routing: Routing<Configuration>): Resolution =
        ChildResolution.child {
            InteropBuilder(
                nodeFactory = { nodeContext -> AppyxNode(nodeContext, routing.configuration.id) },
                integrationPoint = integrationPoint,
            ).build(it)
        }
}

class AppyxNode(
    nodeContext: NodeContext,
    private val s: String,
) : com.bumble.appyx.navigation.node.AbstractNode(
    nodeContext,
) {
    var shouldInterceptBackPress by mutableStateOf(true)

    @Composable
    override fun Content(modifier: Modifier) {
        Box(modifier = modifier.testTag(s)) {
            BackHandler(shouldInterceptBackPress) {
                shouldInterceptBackPress = false
            }
        }
    }
}
