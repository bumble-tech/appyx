package com.github.zsoltk.composeribs.core

import androidx.lifecycle.LifecycleOwner
import com.github.zsoltk.composeribs.core.children.ChildEntryMap
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import kotlinx.coroutines.flow.StateFlow

interface Parent<Routing> : LifecycleOwner {

    val routingSource: RoutingSource<Routing, *>?

    val children: StateFlow<ChildEntryMap<Routing>>

}
