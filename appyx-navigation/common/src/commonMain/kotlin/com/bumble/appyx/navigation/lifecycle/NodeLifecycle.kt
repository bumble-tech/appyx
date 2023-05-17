package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.platform.Lifecycle
import com.bumble.appyx.navigation.platform.LifecycleOwner

interface NodeLifecycle : LifecycleOwner {

    fun updateLifecycleState(state: Lifecycle.State)

}
