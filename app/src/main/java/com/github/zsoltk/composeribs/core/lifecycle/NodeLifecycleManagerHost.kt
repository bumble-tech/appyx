package com.github.zsoltk.composeribs.core.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.github.zsoltk.composeribs.core.children.ChildEntryMap
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import kotlinx.coroutines.flow.StateFlow

internal interface NodeLifecycleManagerHost<Routing> {

    val lifecycleOwner: LifecycleOwner

    val routingSource: RoutingSource<Routing, *>?

    fun children(): StateFlow<ChildEntryMap<Routing>>

}
