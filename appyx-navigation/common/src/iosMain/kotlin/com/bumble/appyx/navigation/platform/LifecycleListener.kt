package com.bumble.appyx.navigation.platform

import com.bumble.appyx.navigation.lifecycle.Lifecycle
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillResignActiveNotification
import platform.UIKit.UIApplicationWillTerminateNotification
import platform.darwin.NSObjectProtocol

class LifecycleListener {
    val lifecycle: PlatformLifecycleRegistry = PlatformLifecycleRegistry()

    private lateinit var didEnterBackgroundNotificationObserver: NSObjectProtocol
    private lateinit var willResignActiveNotificationObserver: NSObjectProtocol
    private lateinit var didBecomeActiveNotificationObserver: NSObjectProtocol
    private lateinit var willTerminateNotificationObserver: NSObjectProtocol

    init {
        created()
        startObserving()
    }

    private fun startObserving() {
        // GOES TO BACKGROUND
        didEnterBackgroundNotificationObserver = addObserverFor(UIApplicationDidEnterBackgroundNotification) {
            created()
        }
        // BECOMES INACTIVE
        willResignActiveNotificationObserver = addObserverFor(UIApplicationWillResignActiveNotification) {
            started()
        }
        // GETS FOCUS BACK / BECOMES ACTIVE
        didBecomeActiveNotificationObserver = addObserverFor(UIApplicationDidBecomeActiveNotification) {
            resumed()
        }
        // IS ABOUT TO BE TERMINATED
        willTerminateNotificationObserver = addObserverFor(UIApplicationWillTerminateNotification) {
            destroyed()
        }
    }

    private fun addObserverFor(notification: platform.Foundation.NSNotificationName, block: () -> Unit) : NSObjectProtocol {
        return NSNotificationCenter.defaultCenter.addObserverForName(
            name = notification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                block()
            }
        )
    }

    private fun created() {
        lifecycle.setCurrentState(Lifecycle.State.CREATED)
    }

    private fun resumed() {
        lifecycle.setCurrentState(Lifecycle.State.RESUMED)
    }

    private fun started() {
        lifecycle.setCurrentState(Lifecycle.State.STARTED)
    }

    private fun destroyed() {
        lifecycle.setCurrentState(Lifecycle.State.DESTROYED)
    }
}
