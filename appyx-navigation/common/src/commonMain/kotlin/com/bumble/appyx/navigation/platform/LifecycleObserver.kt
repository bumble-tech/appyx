package com.bumble.appyx.navigation.platform

/**
 * Ported from Androidx.lifecycle
 *
 * Marks a class as a LifecycleObserver. Don't use this interface directly. Instead implement either DefaultLifecycleObserver or LifecycleEventObserver to be notified about lifecycle events.
 * See Also:
 * Lifecycle - for samples and usage patterns.
 */
interface LifecycleObserver {
}

interface FullLifecycleObserver : LifecycleObserver {
    fun onCreate(owner: LifecycleOwner)
    fun onStart(owner: LifecycleOwner)
    fun onResume(owner: LifecycleOwner)
    fun onPause(owner: LifecycleOwner)
    fun onStop(owner: LifecycleOwner)
    fun onDestroy(owner: LifecycleOwner)
}

/**
 * Ported from Androidx.lifecycle
 *
 * Callback interface for listening to LifecycleOwner state changes.
 * If a class implements both this interface and LifecycleEventObserver, then methods of
 * DefaultLifecycleObserver will be called first, and then followed by the call of
 * LifecycleEventObserver.onStateChanged(LifecycleOwner, Lifecycle.Event)
 *
 * If a class implements this interface and in the same time uses OnLifecycleEvent,
 * then annotations will be ignored.
 */
interface DefaultLifecycleObserver : FullLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {}
    override fun onStart(owner: LifecycleOwner) {}
    override fun onResume(owner: LifecycleOwner) {}
    override fun onPause(owner: LifecycleOwner) {}
    override fun onStop(owner: LifecycleOwner) {}
    override fun onDestroy(owner: LifecycleOwner) {}
}

/**
 * Class that can receive any lifecycle change and dispatch it to the receiver.
 *
 *
 * If a class implements both this interface and
 * [DefaultLifecycleObserver], then
 * methods of `DefaultLifecycleObserver` will be called first, and then followed by the call
 * of [LifecycleEventObserver.onStateChanged]
 *
 *
 * If a class implements this interface and in the same time uses [OnLifecycleEvent], then
 * annotations will be ignored.
 */
fun interface LifecycleEventObserver : LifecycleObserver {
    /**
     * Called when a state transition event happens.
     *
     * @param source The source of the event
     * @param event The event
     */
    fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event)
}
