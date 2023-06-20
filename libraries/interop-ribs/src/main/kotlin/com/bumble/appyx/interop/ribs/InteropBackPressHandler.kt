package com.bumble.appyx.interop.ribs

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.lifecycle.Lifecycle
import com.badoo.ribs.core.plugin.BackPressHandler
import com.badoo.ribs.core.plugin.NodeLifecycleAware

/**
 * When we put Appyx into RIBs, we have the following invocation order of back handlers:
 * 1. All active RIBs handlers using RIBs back press API.
 * 2. All active Appyx handlers using AndroidX back press API.
 *
 * The reason is RIBs integration point implementation
 * where `super<Activity>.onBackPressed()` is invoked after `Node.handleBackPress()`.
 *
 * Let's collect all Appyx back press handlers into a separate [OnBackPressedDispatcher]
 * and use it for `handleBackPress()`.
 */
internal class InteropBackPressHandler :
    BackPressHandler,
    OnBackPressedDispatcherOwner,
    NodeLifecycleAware {

    private val dispatcher = OnBackPressedDispatcher()
    private lateinit var nodeLifecycle: Lifecycle

    override fun onCreate(nodeLifecycle: Lifecycle) {
        this.nodeLifecycle = nodeLifecycle
    }

    override fun handleBackPress(): Boolean =
        if (dispatcher.hasEnabledCallbacks()) {
            dispatcher.onBackPressed()
            true
        } else {
            false
        }

    override val lifecycle: Lifecycle
        get() = nodeLifecycle

    override fun getOnBackPressedDispatcher(): OnBackPressedDispatcher =
        dispatcher

}
