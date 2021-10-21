package com.github.zsoltk.composeribs.core.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import kotlinx.coroutines.flow.StateFlow

internal interface NodeLifecycleManagerHost<Routing> {

    val lifecycleOwner: LifecycleOwner

    val routingSource: RoutingSource<Routing, *>?

    fun child(element: RoutingKey<Routing>): Node.ChildEntry<*>

    fun children(): StateFlow<Map<RoutingKey<Routing>, Node.ChildEntry<Routing>>>

}
