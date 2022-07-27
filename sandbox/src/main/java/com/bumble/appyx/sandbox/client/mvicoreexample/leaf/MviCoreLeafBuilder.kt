package com.bumble.appyx.sandbox.client.mvicoreexample.leaf

import com.bumble.appyx.core.builder.Builder
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature

class MviCoreLeafBuilder : Builder<String>() {

    override fun build(buildContext: BuildContext, payload: String): Node {
        val feature = MviCoreExampleFeature(payload)
        val view = MviCoreLeafViewImpl()
        val interactor = MviCoreLeafInteractor(
            view = view,
            feature = feature
        )

        return MviCoreLeafNode(
            buildContext = buildContext,
            view = view,
            interactor = interactor
        )
    }
}
