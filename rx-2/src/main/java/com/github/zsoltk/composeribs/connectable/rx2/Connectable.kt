package com.github.zsoltk.composeribs.connectable

import com.github.zsoltk.composeribs.core.plugin.NodeLifecycleAware
import com.jakewharton.rxrelay2.Relay

interface Connectable<Input, Output> : NodeLifecycleAware {
    val input: Relay<Input>
    val output: Relay<Output>
}
