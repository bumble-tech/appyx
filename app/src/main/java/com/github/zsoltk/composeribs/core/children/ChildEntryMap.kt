package com.github.zsoltk.composeribs.core.children

import com.github.zsoltk.composeribs.core.routing.RoutingKey

typealias ChildEntryMap<Routing> = Map<RoutingKey<Routing>, ChildEntry<Routing>>
