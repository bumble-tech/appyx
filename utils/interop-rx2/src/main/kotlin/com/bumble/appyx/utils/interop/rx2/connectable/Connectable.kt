package com.bumble.appyx.utils.interop.rx2.connectable

import com.jakewharton.rxrelay2.Relay

interface Connectable<Input, Output> {
    val input: Relay<Input>
    val output: Relay<Output>
}
