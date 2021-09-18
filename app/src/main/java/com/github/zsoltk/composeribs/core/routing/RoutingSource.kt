package com.github.zsoltk.composeribs.core.routing

import androidx.compose.runtime.snapshots.SnapshotStateList

interface RoutingSource<T, S> {

    val elements: SnapshotStateList<RoutingElement<T, S>>

    val pendingRemoval: SnapshotStateList<RoutingElement<T, S>>

    val onScreen: List<RoutingElement<T, S>>

    val offScreen: List<RoutingElement<T, S>>

    // FIXME make sure this isn't getting spammed on transition,
    //  or at least cache value for distinct routing states
    fun canHandleBackPress(): Boolean

    fun onBackPressed()

    fun onRemoved(block: (RoutingKey<T>) -> Unit)

    fun onTransitionFinished(key: RoutingKey<T>, targetState: S)
}
