package com.bumble.appyx.interop.rx2.connectable

import com.bumble.appyx.core.plugin.NodeLifecycleAware
import com.jakewharton.rxrelay2.Relay

interface Connectable<Input, Output> : NodeLifecycleAware {
    val input: Relay<Input>
    val output: Relay<Output>
}
