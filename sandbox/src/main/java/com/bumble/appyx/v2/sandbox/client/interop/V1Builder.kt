package com.bumble.appyx.v2.sandbox.client.interop

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.source.backstack.BackStack
import com.bumble.appyx.v2.sandbox.client.interop.V1Rib.Customisation
import com.bumble.appyx.v2.sandbox.client.interop.V1Router.Configuration
import com.bumble.appyx.v2.sandbox.client.interop.V1Router.Configuration.Content.Main

class V1Builder : SimpleBuilder<V1Rib>() {

    override fun build(buildParams: BuildParams<Nothing?>): V1Rib {
        val customisation = buildParams.getOrDefault(Customisation())
        val backStack = BackStack<Configuration>(
            buildParams = buildParams,
            initialConfiguration = Main
        )
        val router = V1Router(buildParams = buildParams, backStack = backStack)
        val interactor = V1Interactor(buildParams = buildParams, backStack = backStack)
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
        router: V1Router,
        interactor: V1Interactor,
    ) = NodeV1(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory(null),
        plugins = listOf(router, interactor)
    )
}
