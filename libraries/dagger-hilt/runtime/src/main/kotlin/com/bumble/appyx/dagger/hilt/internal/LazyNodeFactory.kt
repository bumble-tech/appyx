package com.bumble.appyx.dagger.hilt.internal

import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

/**
 * This class cannot be internal (as there is an inline function that references it)
 * It is not intended to be used outside the library.
 */
class LazyNodeFactory<N : Node>(private val lazyDelegate: Lazy<NodeFactory<N>>) : NodeFactory<N> {
    override fun create(buildContext: BuildContext): N = lazyDelegate.value.create(buildContext)
}
