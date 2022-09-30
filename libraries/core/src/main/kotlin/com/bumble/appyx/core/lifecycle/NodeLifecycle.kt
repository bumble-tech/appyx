package com.bumble.appyx.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

interface NodeLifecycle : LifecycleOwner {

    /** Used by Portal to take lifecycle ownership of the Node. */
    fun lockCaller(caller: Any? = null)

    fun updateLifecycleState(state: Lifecycle.State, caller: Any? = null)

}
