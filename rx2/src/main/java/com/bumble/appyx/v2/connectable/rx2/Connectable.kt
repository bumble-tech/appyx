package com.bumble.appyx.v2.connectable.rx2

import com.bumble.appyx.v2.core.plugin.NodeLifecycleAware
import com.jakewharton.rxrelay2.Relay

interface Connectable<Input, Output> : NodeLifecycleAware {
    val input: Relay<Input>
    val output: Relay<Output>
}
