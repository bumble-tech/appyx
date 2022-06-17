package com.bumble.appyx.core.routing.source.combined

import com.bumble.appyx.core.routing.RoutingSource

operator fun <Routing> RoutingSource<Routing, *>.plus(
    other: RoutingSource<Routing, *>,
): CombinedRoutingSource<Routing> {
    val currentSources = if (this is CombinedRoutingSource<Routing>) sources else listOf(this)
    val otherSources = if (other is CombinedRoutingSource<Routing>) other.sources else listOf(other)
    return CombinedRoutingSource(currentSources + otherSources)
}
