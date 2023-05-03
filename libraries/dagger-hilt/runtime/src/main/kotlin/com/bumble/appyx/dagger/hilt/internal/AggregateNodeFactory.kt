package com.bumble.appyx.dagger.hilt.internal

import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.dagger.hilt.NodeFactoryProvider
import com.bumble.appyx.dagger.hilt.getCustomNodeFactory
import com.bumble.appyx.dagger.hilt.getNodeFactory

/**
 * This class cannot be internal (as the generated kotlin classes need to reference it)
 * It is not intended to be used outside the library.
 */
interface AggregateNodeFactory {
    val nodeFactoryProvider: NodeFactoryProvider
}

/**
 * This function is used by the hilt-compiler.
 */
inline fun <reified T : Node> AggregateNodeFactory.nodeFactory(): Lazy<NodeFactory<T>> =
    lazy { nodeFactoryProvider.getNodeFactory() }

/**
 * This function is used by the hilt-compiler.
 */
inline fun <reified T> AggregateNodeFactory.customNodeFactory(): Lazy<T> =
    lazy { nodeFactoryProvider.getCustomNodeFactory() }
