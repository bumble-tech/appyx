package com.github.zsoltk.composeribs.core.minimal.reactive

interface Source<out T> {
    fun observe(callback: (T) -> Unit): Cancellable
}
