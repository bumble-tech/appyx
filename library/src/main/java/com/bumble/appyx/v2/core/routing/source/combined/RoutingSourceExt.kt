package com.bumble.appyx.v2.core.routing.source.combined

import com.bumble.appyx.v2.core.routing.RoutingSource

operator fun <Key> RoutingSource<Key, *>.plus(
    other: RoutingSource<Key, *>,
): CombinedRoutingSource<Key> {
    val currentSources = if (this is CombinedRoutingSource<Key>) sources else listOf(this)
    val otherSources = if (other is CombinedRoutingSource<Key>) other.sources else listOf(other)
    return CombinedRoutingSource(currentSources + otherSources)
}
