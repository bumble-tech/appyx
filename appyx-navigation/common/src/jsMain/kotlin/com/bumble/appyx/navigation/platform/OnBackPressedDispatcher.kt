package com.bumble.appyx.navigation.platform

import androidx.compose.ui.util.fastForEachReversed

/**
 * Adapted from Android's OnBackPressedDispatcher.
 *
 * Create a new OnBackPressedDispatcher that dispatches [.onBackPressed] events
 * to one or more [OnBackPressedCallback] instances.
 *
 * @param fallbackOnBackPressed The Runnable that should be triggered if
 * [.onBackPressed] is called when no [OnBackPressedCallback] have been registered.
 */
class OnBackPressedDispatcher(private val fallbackOnBackPressed: (() -> Unit)? = null) {
    val onBackPressedCallbacks: ArrayDeque<OnBackPressedCallback> =
        ArrayDeque()

    /**
     * Internal implementation of [.addCallback] that gives
     * access to the [Cancellable] that specifically removes this callback from
     * the dispatcher without relying on [OnBackPressedCallback.remove] which
     * is what external developers should be using.
     *
     * @param onBackPressedCallback The callback to add
     * @return a [Cancellable] which can be used to [cancel][Cancellable.cancel]
     * the callback and remove it from the set of OnBackPressedCallbacks.
     */
    fun addCancellableCallback(onBackPressedCallback: OnBackPressedCallback): Cancellable {
        onBackPressedCallbacks.add(onBackPressedCallback)
        val cancellable = OnBackPressedCancellable(onBackPressedCallback)
        onBackPressedCallback.addCancellable(cancellable)
        return cancellable
    }

    /**
     * Trigger a call to the currently added [callbacks][OnBackPressedCallback] in reverse
     * order in which they were added. Only if the most recently added callback is not
     * [enabled][OnBackPressedCallback.isEnabled]
     * will any previously added callback be called.
     *
     *
     * If [.hasEnabledCallbacks] is `false` when this method is called, the
     * fallback Runnable set by [the constructor][.OnBackPressedDispatcher]
     * will be triggered.
     */
    fun onBackPressed() {
        onBackPressedCallbacks.fastForEachReversed { callback ->
            if (callback.isEnabled) {
                callback.handleOnBackPressed()
                return
            }
        }
        fallbackOnBackPressed?.invoke()
    }

    private inner class OnBackPressedCancellable(onBackPressedCallback: OnBackPressedCallback) :
        Cancellable {
        private val onBackPressedCallback: OnBackPressedCallback

        init {
            this.onBackPressedCallback = onBackPressedCallback
        }

        override fun cancel() {
            onBackPressedCallbacks.remove(onBackPressedCallback)
            onBackPressedCallback.removeCancellable(this)
        }
    }
}
