package com.bumble.appyx.utils.interop.rx3.connectable

import com.jakewharton.rxrelay3.Relay

interface Connectable<Input, Output> {
    val input: Relay<Input>
    val output: Relay<Output>
}
