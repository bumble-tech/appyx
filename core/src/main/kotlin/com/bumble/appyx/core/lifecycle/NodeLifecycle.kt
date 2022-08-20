package com.bumble.appyx.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

interface NodeLifecycle : LifecycleOwner {

    fun updateLifecycleState(state: Lifecycle.State)

}
