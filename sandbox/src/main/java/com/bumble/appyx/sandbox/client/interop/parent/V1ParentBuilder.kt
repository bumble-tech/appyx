package com.bumble.appyx.sandbox.client.interop.parent

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.backstack.BackStack
import com.bumble.appyx.sandbox.client.interop.parent.V1ParentRib.Customisation
import com.bumble.appyx.sandbox.client.interop.parent.V1ParentRouter.Configuration
import com.bumble.appyx.sandbox.client.interop.parent.V1ParentRouter.Configuration.InteropNode

class V1ParentBuilder : SimpleBuilder<V1ParentRib>() {

    override fun build(buildParams: BuildParams<Nothing?>): V1ParentRib {
        val customisation = buildParams.getOrDefault(Customisation())
        val backStack = BackStack<Configuration>(
            buildParams = buildParams,
            initialConfiguration = InteropNode
        )
        val router = V1ParentRouter(buildParams = buildParams, backStack = backStack)
        val interactor = V1ParentInteractor(buildParams = buildParams, backStack = backStack)
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
        router: V1ParentRouter,
        interactor: V1ParentInteractor,
    ) = V1ParentNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory(null),
        plugins = listOf(router, interactor)
    )
}
