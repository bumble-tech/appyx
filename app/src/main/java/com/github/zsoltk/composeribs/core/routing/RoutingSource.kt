package com.github.zsoltk.composeribs.core.routing

import androidx.compose.runtime.snapshots.SnapshotStateList

interface RoutingSource<T, S> {

    val elements: SnapshotStateList<RoutingElement<T, S>>

    val pendingRemoval: SnapshotStateList<RoutingElement<T, S>>

    val onScreen: List<RoutingElement<T, S>>

    val offScreen: List<RoutingElement<T, S>>

    fun onRemoved(block: (RoutingKey<T>) -> Unit)

    fun onTransitionFinished(key: RoutingKey<T>, targetState: S)
}
