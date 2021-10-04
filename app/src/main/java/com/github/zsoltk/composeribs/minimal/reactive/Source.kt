package com.github.zsoltk.composeribs.minimal.reactive

interface Source<out T> {
    fun observe(callback: (T) -> Unit): Cancellable
}
