package com.bumble.appyx.v2.core.minimal.reactive

interface Source<out T> {
    fun observe(callback: (T) -> Unit): Cancellable
}
