package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.platform.PlatformLifecycle
import com.bumble.appyx.navigation.platform.PlatformLifecycleOwner

interface NodeLifecycle : PlatformLifecycleOwner {

    fun updateLifecycleState(state: PlatformLifecycle.State)

}
