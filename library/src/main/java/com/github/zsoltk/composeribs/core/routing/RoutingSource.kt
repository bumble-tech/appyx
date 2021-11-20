package com.github.zsoltk.composeribs.core.routing

import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface RoutingSource<Key, State> : UpNavigationHandler {

    val adapter: RoutingSourceAdapter<Key, State>

    val elements: StateFlow<RoutingElements<Key, out State>>

    val canHandleBackPress: StateFlow<Boolean>

    fun onBackPressed()

    fun onTransitionFinished(key: RoutingKey<Key>)

    /**
     * Bundle for future state restoration.
     * Result should be supported by [androidx.compose.runtime.saveable.SaverScope.canBeSaved].
     */
    fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {}

    override fun handleUpNavigation(): Boolean =
        canHandleBackPress.value.also { if (it) onBackPressed() }

}

fun <Key, State> RoutingSource<Key, State>.adapter(
    scope: CoroutineScope,
    onScreenResolver: OnScreenResolver<State>
) = SingleRoutingSourceAdapter(
    scope,
    routingSource = this,
    onScreenResolver = onScreenResolver
)
