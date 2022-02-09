package com.bumble.appyx.v2.sandbox.interop

import com.badoo.ribs.builder.Builder
import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.v2.sandbox.interop.NodeV1Builder.RootParams

class NodeV1Builder : Builder<RootParams, RibV1>() {

    class RootParams

    override fun build(buildParams: BuildParams<RootParams>): RibV1 {
        val customisation = buildParams.getOrDefault(RibV1.Customisation())

        val router = RouterV1(buildParams = buildParams)

        return node(
            buildParams = buildParams,
            customisation = customisation,
            router = router,
        )
    }


    private fun node(
        buildParams: BuildParams<RootParams>,
        customisation: RibV1.Customisation,
        router: RouterV1,
    ) = NodeV1(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory(null),
        plugins = listOf(router)
    )
}
