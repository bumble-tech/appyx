package com.bumble.appyx.utils.interop.ribs

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.navigation.integration.NodeFactory
import com.bumble.appyx.navigation.integration.IntegrationPoint
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.build
import com.bumble.appyx.utils.interop.ribs.InteropNodeImpl.Companion.InteropNodeKey

class InteropBuilder<N : Node>(
    private val nodeFactory: NodeFactory<N>,
    private val integrationPoint: IntegrationPoint
) : SimpleBuilder<InteropNode<N>>() {

    override fun build(buildParams: BuildParams<Nothing?>): InteropNode<N> {
        val bundle = buildParams.savedInstanceState?.getBundle(InteropNodeKey)
        val stateMap = bundle?.let {
            val keys = bundle.keySet()
            val map = mutableMapOf<String, Any?>()
            keys.forEach {
                map[it] = bundle[it]
            }
            map
        }

        val appyxNode = nodeFactory
            .create(
                buildContext = BuildContext.root(
                    savedStateMap = stateMap,
                    customisations = buildParams.buildContext.customisations
                )
            )
            .apply { integrationPoint = this@InteropBuilder.integrationPoint }
            .build()

        return InteropNodeImpl(buildParams = buildParams, appyxNode = appyxNode)
    }
}
