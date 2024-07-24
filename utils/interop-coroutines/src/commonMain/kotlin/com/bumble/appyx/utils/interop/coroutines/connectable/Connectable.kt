package com.bumble.appyx.utils.interop.coroutines.connectable

import kotlinx.coroutines.flow.MutableSharedFlow

interface Connectable<Input, Output> {
    val input: MutableSharedFlow<Input>
    val output: MutableSharedFlow<Output>
}
