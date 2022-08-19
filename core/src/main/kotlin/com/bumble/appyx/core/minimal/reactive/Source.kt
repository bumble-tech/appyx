package com.bumble.appyx.core.minimal.reactive

interface Source<out T> {
    fun observe(callback: (T) -> Unit): Cancellable
}
