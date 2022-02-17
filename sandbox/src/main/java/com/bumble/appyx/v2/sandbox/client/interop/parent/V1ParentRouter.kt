package com.bumble.appyx.v2.sandbox.client.interop.parent

import android.os.Parcelable
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.resolution.ChildResolution.Companion.child
import com.badoo.ribs.routing.resolution.Resolution
import com.badoo.ribs.routing.router.Router
import com.badoo.ribs.routing.source.backstack.BackStack
import com.bumble.appyx.interop.v1v2.V1V2Builder
import com.bumble.appyx.v2.sandbox.client.container.ContainerNode
import com.bumble.appyx.v2.sandbox.client.interop.child.V1ChildNode
import com.bumble.appyx.v2.sandbox.client.interop.parent.V1ParentRouter.Configuration
import com.bumble.appyx.v2.sandbox.client.interop.parent.V1ParentRouter.Configuration.V1Node
import kotlinx.android.parcel.Parcelize

internal class V1ParentRouter(
    backStack: BackStack<Configuration>,
    private val buildParams: BuildParams<*>,
) : Router<Configuration>(
    buildParams = buildParams,
    routingSource = backStack
) {

    sealed class Configuration : Parcelable {
        @Parcelize
        object InteropNode : Configuration()

        @Parcelize
        object V1Node : Configuration()
    }

    override fun resolve(routing: Routing<Configuration>): Resolution =
        when (routing.configuration) {
            is Configuration.InteropNode ->
                child {
                    V1V2Builder(nodeFactory = { buildContext -> ContainerNode(buildContext) })
                        .build(it)
                }
            is V1Node -> {
                child {
                    V1ChildNode(
                        buildParams = BuildParams(
                            payload = buildParams.payload,
                            buildContext = it
                        )
                    )
                }
            }
        }
}
