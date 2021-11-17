package com.github.zsoltk.composeribs.core.routing

import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler
import kotlinx.coroutines.flow.StateFlow

interface RoutingSource<Key, out State> : UpNavigationHandler {

    val all: StateFlow<RoutingElements<Key, out State>>

    val onScreen: StateFlow<RoutingElements<Key, out State>>

    val offScreen: StateFlow<RoutingElements<Key, out State>>

    val canHandleBackPress: StateFlow<Boolean>

    fun onBackPressed()

    fun onTransitionFinished(key: RoutingKey<Key>)

    /**
     * Bundle for future state restoration.
     * Result should be supported by [androidx.compose.runtime.saveable.SaverScope.canBeSaved].
     */
    fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {}

    /**
     * @return [key] should be rendered on the screen based on its [State].
     */
    fun isOnScreen(key: RoutingKey<Key>): Boolean =
        onScreen.value.any { it.key == key }

    override fun handleUpNavigation(): Boolean =
        canHandleBackPress.value.also { if (it) onBackPressed() }

}
