package com.bumble.appyx.navigation.platform

import androidx.compose.runtime.AtomicReference

interface Cancellable {
    /**
     * Cancel the subscription. This call should be idempotent, making it safe to
     * call multiple times.
     */
    fun cancel()
}

/**
 * Create a [OnBackPressedCallback].
 *
 * @param isEnabled The default enabled state for this callback.
 * @see .setEnabled
 */
abstract class OnBackPressedCallback(
    /**
     * Set the enabled state of the callback. Only when this callback
     * is enabled will it receive callbacks to [.handleOnBackPressed].
     *
     * @param isEnabled whether the callback should be considered enabled
     */
    var isEnabled: Boolean
) {
    /**
     * Checks whether this callback should be considered enabled. Only when this callback
     * is enabled will it receive callbacks to [.handleOnBackPressed].
     *
     * @return Whether this callback should be considered enabled.
     */
    private val cancellablesReference: AtomicReference<List<Cancellable>> =
        AtomicReference(emptyList())

    /**
     * Removes this callback from any [OnBackPressedDispatcher] it is currently
     * added to.
     */
    fun remove() {
        for (cancellable in cancellablesReference.get()) {
            cancellable.cancel()
        }
    }

    /**
     * Callback for handling the [OnBackPressedDispatcher.onBackPressed] event.
     */
    abstract fun handleOnBackPressed()
    fun addCancellable(cancellable: Cancellable) {
        cancellablesReference.set(cancellablesReference.get() + cancellable)
    }

    fun removeCancellable(cancellable: Cancellable) {
        cancellablesReference.set(cancellablesReference.get() - cancellable)
    }
}
