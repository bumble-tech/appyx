package com.bumble.appyx.interop.ribs

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.build
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.interop.ribs.InteropNodeImpl.Companion.InteropNodeKey

class InteropBuilder<N : Node>(
    private val nodeFactory: NodeFactory<N>,
    private val integrationPoint: IntegrationPoint
) : SimpleBuilder<InteropNode<N>>() {

    override fun build(buildParams: BuildParams<Nothing?>): InteropNode<N> {
        val stateMap = buildAppyxSavedStateMap(buildParams)
        val integrationPointDelegate = UpHandlingAppyxIntegrationPoint(integrationPoint)

        val appyxNode = nodeFactory
            .create(
                buildContext = BuildContext.root(
                    savedStateMap = stateMap,
                    customisations = buildParams.buildContext.customisations
                )
            )
            .apply { integrationPoint = integrationPointDelegate }
            .build()

        return InteropNodeImpl(buildParams = buildParams, appyxNode = appyxNode)
            .apply {
                integrationPointDelegate.setUpNavigationListener(::upNavigation)
            }
    }

    private fun buildAppyxSavedStateMap(buildParams: BuildParams<Nothing?>): SavedStateMap? {
        val bundle = buildParams.savedInstanceState?.getBundle(InteropNodeKey)
        val stateMap = bundle?.let {
            val keys = bundle.keySet()
            val map = mutableMapOf<String, Any?>()
            keys.forEach {
                map[it] = bundle[it]
            }
            map
        }
        return stateMap
    }

}
