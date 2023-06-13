package com.bumble.appyx.navigation.lifecycle

interface NodeLifecycle : CommonLifecycleOwner {

    fun updateLifecycleState(state: Lifecycle.State)

}
