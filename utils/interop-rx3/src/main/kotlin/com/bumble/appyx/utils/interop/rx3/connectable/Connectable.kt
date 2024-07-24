package com.bumble.appyx.utils.interop.rx3.connectable

import com.bumble.appyx.navigation.plugin.Tooling
import com.jakewharton.rxrelay3.Relay

interface Connectable<Input, Output> : Tooling {
    val input: Relay<Input>
    val output: Relay<Output>
}
