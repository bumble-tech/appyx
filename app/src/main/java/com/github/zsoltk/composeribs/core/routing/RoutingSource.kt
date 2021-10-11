package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable
import kotlinx.coroutines.flow.StateFlow

interface RoutingSource<Key : Parcelable, State : Parcelable> {

    val all: StateFlow<List<RoutingElement<Key, State>>>

    val onScreen: StateFlow<List<RoutingElement<Key, State>>>

    val offScreen: StateFlow<List<RoutingElement<Key, State>>>

    val canHandleBackPress: StateFlow<Boolean>

    fun onBackPressed()

    fun onTransitionFinished(key: RoutingKey<Key>)

    /**
     * Bundle for future state restoration.
     * Result should be supported by [androidx.compose.runtime.saveable.SaverScope.canBeSaved].
     */
    fun saveInstanceState(): Any? = null

}
