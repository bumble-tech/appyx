package com.bumble.appyx.navigation.platform

/**
 * Ported from Androidx.lifecycle
 *
 * Marks a class as a LifecycleObserver. Don't use this interface directly. Instead implement either DefaultLifecycleObserver or LifecycleEventObserver to be notified about lifecycle events.
 * See Also:
 * Lifecycle - for samples and usage patterns.
 */
interface PlatformLifecycleObserver {
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
 *
 * Note that this port does not include the lifecycle owner that the lifecycle events are associated
 * with, as this is a complexity that has been avoided at this point.
 */
interface DefaultPlatformLifecycleObserver : PlatformLifecycleObserver {
    fun onCreate() {}
    fun onStart() {}
    fun onResume() {}
    fun onPause() {}
    fun onStop() {}
    fun onDestroy() {}
}

/**
 * Class that can receive any lifecycle change and dispatch it to the receiver.
 *
 *
 * If a class implements both this interface and
 * [DefaultPlatformLifecycleObserver], then
 * methods of `DefaultLifecycleObserver` will be called first, and then followed by the call
 * of [PlatformLifecycleEventObserver.onStateChanged]
 *
 *
 * If a class implements this interface and in the same time uses [OnLifecycleEvent], then
 * annotations will be ignored.
 */
fun interface PlatformLifecycleEventObserver : PlatformLifecycleObserver {
    /**
     * Called when a state transition event happens.
     *
     * @param event The event
     */
    fun onStateChanged(newState: PlatformLifecycle.State, event: PlatformLifecycle.Event)
}
