package com.bumble.appyx.dagger.hilt.internal

import com.bumble.appyx.dagger.hilt.NodeFactoryProvider

/**
 * This class cannot be internal (as the generated kotlin classes need to reference it)
 * It is not intended to be used outside the library.
 */
interface AggregateNodeFactoryBuilder<T> {
    fun build(nodeFactoryProvider: NodeFactoryProvider): T
}
