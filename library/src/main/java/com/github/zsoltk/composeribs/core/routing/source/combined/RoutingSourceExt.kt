package com.github.zsoltk.composeribs.core.routing.source.combined

import com.github.zsoltk.composeribs.core.routing.RoutingSource

operator fun <Key> RoutingSource<Key, *>.plus(other: RoutingSource<Key, *>): RoutingSource<Key, Any> {
    val currentSources = if (this is CombinedRoutingSource<Key>) sources else listOf(this)
    val otherSources = if (other is CombinedRoutingSource<Key>) other.sources else listOf(other)
    return CombinedRoutingSource(currentSources + otherSources)
}
