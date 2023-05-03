package com.bumble.appyx.dagger.hilt.internal

import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.dagger.hilt.NodeFactoryProvider
import dagger.Lazy
import javax.inject.Provider

/**
 * This class cannot be internal (as the generated kotlin classes need to reference it)
 * It is not intended to be used outside the library.
 */
class HiltNodeFactoryProvider(
    private val hiltFactoriesProviderMap: Lazy<Map<Class<*>, Provider<AggregateNodeFactoryBuilder<*>>>>,
    private val nodeFactoryMap: Lazy<Map<Class<*>, Provider<NodeFactory<*>>>>,
    private val customNodeFactoryMap: Lazy<Map<Class<*>, Provider<Any>>>
) : NodeFactoryProvider {

    override fun <T> getAggregateNodeFactory(clazz: Class<T>): T =
        (hiltFactoriesProviderMap.get().getValue(clazz) as Provider<AggregateNodeFactoryBuilder<T>>)
            .get()
            .build(this)

    override fun <T : Node> getNodeFactory(clazz: Class<T>): NodeFactory<T> =
        (nodeFactoryMap.get().getValue(clazz) as Provider<NodeFactory<T>>).get()

    override fun <T> getCustomNodeFactory(
        clazz: Class<T>
    ): T = (customNodeFactoryMap.get().getValue(clazz) as Provider<T>).get()
}
