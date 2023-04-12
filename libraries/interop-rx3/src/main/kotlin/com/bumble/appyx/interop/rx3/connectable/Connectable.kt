package com.bumble.appyx.interop.rx3.connectable

import com.bumble.appyx.navigation.plugin.NodeLifecycleAware
import com.jakewharton.rxrelay3.Relay

interface Connectable<Input, Output> : NodeLifecycleAware {
    val input: Relay<Input>
    val output: Relay<Output>
}
