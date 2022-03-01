package com.bumble.appyx.v2.sandbox.client.mvicoreexample

import com.bumble.appyx.v2.core.builder.Builder
import com.bumble.appyx.v2.core.modality.BuildContext
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature

class MviCoreExampleBuilder : Builder<String>() {

    override fun build(buildContext: BuildContext, payload: String): Node {
        val feature = MviCoreExampleFeature(payload)

        val backStack = BackStack<Routing>(
            initialElement = Routing.Child1,
            savedStateMap = buildContext.savedStateMap,
        )
        val view = MviCoreExampleView(backStack = backStack)
        val interactor = MviCoreExampleInteractor(feature = feature, backStack = backStack, view = view)

        return MviCoreExampleNode(
            buildContext = buildContext,
            backStack = backStack,
            view = view,
            plugins = listOf(interactor, view)
        )
    }
}
