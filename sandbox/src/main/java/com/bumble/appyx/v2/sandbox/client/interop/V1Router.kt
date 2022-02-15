package com.bumble.appyx.v2.sandbox.client.interop

import android.os.Parcelable
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.resolution.ChildResolution.Companion.child
import com.badoo.ribs.routing.resolution.Resolution
import com.badoo.ribs.routing.router.Router
import com.badoo.ribs.routing.source.RoutingSource
import com.bumble.appyx.interop.v1v2.V1V2Node
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.build
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode
import com.bumble.appyx.v2.sandbox.client.interop.V1Router.Configuration
import kotlinx.android.parcel.Parcelize

internal class V1Router(
    private val buildParams: BuildParams<*>,
) : Router<Configuration>(
    buildParams = buildParams,
    routingSource = RoutingSource.permanent(Configuration.Content.Main)
) {

    sealed class Configuration : Parcelable {
        sealed class Content : Configuration() {
            @Parcelize
            object Main : Content()
        }
    }

    override fun resolve(routing: Routing<Configuration>): Resolution =
        when (routing.configuration) {
            is Configuration.Content.Main ->
                child {
                    V1V2Node(
                        buildParams = BuildParams(payload = buildParams.payload, buildContext = it),
                        v2Node = ContainerNode(buildContext = BuildContext.root(savedStateMap = null)).build()
                    )
                }
        }
}
