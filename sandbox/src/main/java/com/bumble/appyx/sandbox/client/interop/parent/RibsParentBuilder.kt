package com.bumble.appyx.sandbox.client.interop.parent

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.backstack.BackStack
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentRib.Customisation
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentRouter.Configuration
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentRouter.Configuration.InteropNode

class RibsParentBuilder : SimpleBuilder<RibsParentRib>() {

    override fun build(buildParams: BuildParams<Nothing?>): RibsParentRib {
        val customisation = buildParams.getOrDefault(Customisation())
        val backStack = BackStack<Configuration>(
            buildParams = buildParams,
            initialConfiguration = InteropNode
        )
        val router = RibsParentRouter(buildParams = buildParams, backStack = backStack)
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
