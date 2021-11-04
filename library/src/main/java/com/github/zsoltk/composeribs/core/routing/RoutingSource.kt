package com.github.zsoltk.composeribs.core.routing

import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler
import kotlinx.coroutines.flow.StateFlow

interface RoutingSource<Key, State> : UpNavigationHandler {

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

    /**
     * @return [key] should be rendered on the screen based on its [State].
     */
    fun isOnScreen(key: RoutingKey<Key>): Boolean = true

    override fun handleUpNavigation(): Boolean =
        canHandleBackPress.value.also { if (it) onBackPressed() }

}
