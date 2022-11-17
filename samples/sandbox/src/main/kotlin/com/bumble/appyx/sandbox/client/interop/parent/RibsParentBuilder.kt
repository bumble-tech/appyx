package com.bumble.appyx.sandbox.client.interop.parent

import androidx.annotation.VisibleForTesting
import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.backstack.BackStack
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentRib.Customisation
import com.bumble.appyx.sandbox.client.interop.parent.routing.RibsParentChildBuilders
import com.bumble.appyx.sandbox.client.interop.parent.routing.RibsParentChildBuildersImpl
import com.bumble.appyx.sandbox.client.interop.parent.routing.RibsParentRouter
import com.bumble.appyx.sandbox.client.interop.parent.routing.RibsParentRouter.Configuration
import com.bumble.appyx.sandbox.client.interop.parent.routing.RibsParentRouter.Configuration.InteropNode

class RibsParentBuilder @VisibleForTesting internal constructor(
    private val childBuilders: RibsParentChildBuilders?,
    private val integrationPoint: IntegrationPoint
) : SimpleBuilder<RibsParentRib>() {

    constructor(integrationPoint: IntegrationPoint) : this(
        childBuilders = null,
        integrationPoint = integrationPoint
    )

    override fun build(buildParams: BuildParams<Nothing?>): RibsParentRib {
        val customisation = buildParams.getOrDefault(Customisation())
        val backStack = BackStack<Configuration>(
            buildParams = buildParams,
            initialConfiguration = InteropNode
        )
        val router = RibsParentRouter(
            buildParams = buildParams,
            backStack = backStack,
            childBuilders = childBuilders ?: RibsParentChildBuildersImpl(),
            integrationPoint = integrationPoint
        )
        val interactor = RibsParentInteractor(buildParams = buildParams, backStack = backStack)
        return node(
            buildParams = buildParams,
            customisation = customisation,
            router = router,
            interactor = interactor
        )
    }

    private fun node(
        buildParams: BuildParams<*>,
        customisation: Customisation,
        router: RibsParentRouter,
        interactor: RibsParentInteractor,
    ) = RibsParentNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory(null),
        plugins = listOf(router, interactor)
    )
}
