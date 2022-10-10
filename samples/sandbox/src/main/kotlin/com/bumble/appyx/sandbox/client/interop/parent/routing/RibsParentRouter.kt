package com.bumble.appyx.sandbox.client.interop.parent.routing

import android.os.Parcelable
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.resolution.ChildResolution.Companion.child
import com.badoo.ribs.routing.resolution.Resolution
import com.badoo.ribs.routing.router.Router
import com.badoo.ribs.routing.source.backstack.BackStack
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.interop.ribs.InteropBuilder
import com.bumble.appyx.sandbox.client.interop.parent.routing.RibsParentRouter.Configuration
import com.bumble.appyx.sandbox.client.interop.parent.routing.RibsParentRouter.Configuration.InteropNode
import com.bumble.appyx.sandbox.client.interop.parent.routing.RibsParentRouter.Configuration.RibsNode
import kotlinx.android.parcel.Parcelize

internal class RibsParentRouter(
    private val childBuilders: RibsParentChildBuilders,
    backStack: BackStack<Configuration>,
    buildParams: BuildParams<*>,
    private val integrationPoint: IntegrationPoint
) : Router<Configuration>(
    buildParams = buildParams,
    routingSource = backStack
) {

    sealed class Configuration : Parcelable {
        @Parcelize
        object InteropNode : Configuration()

        @Parcelize
        object RibsNode : Configuration()
    }

    override fun resolve(routing: Routing<Configuration>): Resolution =
        with(childBuilders) {
            when (routing.configuration) {
                is InteropNode ->
                    child {
                        InteropBuilder(
                            nodeFactory = { buildContext -> interopNode.build(buildContext) },
                            integrationPoint = integrationPoint
                        ).build(it)
                    }
                is RibsNode -> {
                    child { ribsNode.build(it, null) }
                }
            }
        }
}
