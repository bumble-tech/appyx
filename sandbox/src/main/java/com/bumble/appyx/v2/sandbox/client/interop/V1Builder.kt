package com.bumble.appyx.v2.sandbox.client.interop

import com.badoo.ribs.builder.Builder
import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.v2.sandbox.client.interop.V1Builder.RootParams
import com.bumble.appyx.v2.sandbox.client.interop.V1Rib.Customisation

class V1Builder : Builder<RootParams, V1Rib>() {

    class RootParams

    override fun build(buildParams: BuildParams<RootParams>): V1Rib {
        val customisation = buildParams.getOrDefault(Customisation())
        val router = V1Router(buildParams = buildParams)
        return node(
            buildParams = buildParams,
            customisation = customisation,
            router = router,
        )
    }

    private fun node(
        buildParams: BuildParams<RootParams>,
        customisation: Customisation,
        router: V1Router,
    ) = NodeV1(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory(null),
        plugins = listOf(router)
    )
}
