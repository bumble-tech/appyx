package com.bumble.appyx.v2.core.children

import com.bumble.appyx.v2.core.routing.RoutingKey

typealias ChildEntryMap<Routing> = Map<RoutingKey<Routing>, ChildEntry<Routing>>
