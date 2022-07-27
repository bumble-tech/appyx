package com.bumble.appyx.sandbox.client.mvicoreexample

import com.bumble.appyx.core.builder.Builder
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing.Child1
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature

class MviCoreExampleBuilder : Builder<String>() {

    override fun build(buildContext: BuildContext, payload: String): Node {
        val feature = MviCoreExampleFeature(payload)

        val backStack = BackStack<Routing>(
            initialElement = Child1,
            savedStateMap = buildContext.savedStateMap,
        )
        val view = MviCoreExampleViewImpl(backStack = backStack)
        val interactor = MviCoreExampleInteractor(
            feature = feature,
            backStack = backStack,
            view = view
        )

        return MviCoreExampleNode(
            buildContext = buildContext,
            backStack = backStack,
            view = view,
            plugins = listOf(interactor)
        )
    }
}
