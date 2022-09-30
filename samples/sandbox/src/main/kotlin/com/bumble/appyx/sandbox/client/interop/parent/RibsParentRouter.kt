package com.bumble.appyx.sandbox.client.interop.parent

import android.os.Parcelable
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.resolution.ChildResolution.Companion.child
import com.badoo.ribs.routing.resolution.Resolution
import com.badoo.ribs.routing.router.Router
import com.badoo.ribs.routing.source.backstack.BackStack
import com.bumble.appyx.interop.ribs.InteropBuilder
import com.bumble.appyx.sandbox.client.container.ContainerNode
import com.bumble.appyx.sandbox.client.interop.child.RibsChildNode
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentRouter.Configuration
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentRouter.Configuration.RibsNode
import kotlinx.android.parcel.Parcelize

internal class RibsParentRouter(
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
        object RibsNode : Configuration()
    }

    override fun resolve(routing: Routing<Configuration>): Resolution =
        when (routing.configuration) {
            is Configuration.InteropNode ->
                child {
                    InteropBuilder(
                        nodeFactory = { buildContext ->
                            TODO()
                        }
                    ).build(it)
                }
            is RibsNode -> {
                child {
                    RibsChildNode(
                        buildParams = BuildParams(
                            payload = buildParams.payload,
                            buildContext = it
                        )
                    )
                }
            }
        }
}
