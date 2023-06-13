package com.bumble.appyx.navigation.plugin

import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.navigation.lifecycle.Lifecycle
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.plugin.BackPressHandler.OnBackPressedCallback

inline fun <reified P : Plugin> Node.plugins(): List<P> =
    this.plugins.filterIsInstance<P>()

interface NodeAware<N : Node> : NodeReadyObserver<N> {
    val node: N
}

interface NodeReadyObserver<N : Node> : Plugin {
    fun init(node: N) {}
}

interface NodeLifecycleAware : Plugin {
    fun onCreate(lifecycle: Lifecycle) {}
}

interface UpNavigationHandler : Plugin {
    fun handleUpNavigation(): Boolean = false
}

fun interface Destroyable : Plugin {
    fun destroy()
}

/**
 * Implementing class can handle back presses via [OnBackPressedCallback].
 *
 * Implement either [onBackPressedCallback] or [onBackPressedCallbackList], not both.
 * In case if both implemented, [onBackPressedCallback] will be ignored.
 * There is runtime check in [Node] to verify correctness.
 */
interface BackPressHandler : Plugin {

    val onBackPressedCallback: OnBackPressedCallback? get() = null

    /** It is impossible to combine multiple [OnBackPressedCallback] into the single one, they are not observable. */
    val onBackPressedCallbackList: List<OnBackPressedCallback>
        get() = listOfNotNull(onBackPressedCallback)

    fun handleOnBackPressed(): Boolean =
        onBackPressedCallbackList.any { callback ->
            val isEnabled = callback.isEnabled
            if (isEnabled) callback.handleOnBackPressed()
            isEnabled
        }

    interface OnBackPressedCallback {
        val isEnabled: Boolean
        fun handleOnBackPressed()
    }
}
