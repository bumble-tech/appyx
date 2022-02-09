package com.bumble.appyx.v2.sandbox.interop

import android.os.Parcelable
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.resolution.ChildResolution.Companion.child
import com.badoo.ribs.routing.resolution.Resolution
import com.badoo.ribs.routing.router.Router
import com.badoo.ribs.routing.source.RoutingSource
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode
import com.bumble.appyx.v2.sandbox.interop.RouterV1.Configuration
import kotlinx.android.parcel.Parcelize

internal class RouterV1(
    buildParams: BuildParams<*>,
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
        when (val configuration = routing.configuration) {
            is Configuration.Content.Main ->
                child {
                    InteropNode(
                        buildParams = BuildParams(payload = null, buildContext = it),
                        nodeFactory = { buildContext -> ContainerNode(buildContext) }
                    )
                }
        }
}