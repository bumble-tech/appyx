package com.bumble.appyx.interop.ribsappyx

import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.bumble.appyx.interop.ribsappyx.InteropNodeImpl.Companion.InteropNodeKey
import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.build

class InteropBuilder<N : Node>(
    private val nodeFactory: NodeFactory<N>
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
            .build()

        return InteropNodeImpl(buildParams = buildParams, appyxNode = appyxNode)
    }
}
