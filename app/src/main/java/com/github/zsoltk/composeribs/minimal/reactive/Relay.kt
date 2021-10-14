package com.github.zsoltk.composeribs.minimal.reactive

import com.github.zsoltk.composeribs.minimal.reactive.Cancellable.Companion.cancellableOf


open class Relay<T> : Source<T>, Emitter<T> {
    // keep list read-only to avoid modification during iteration
    private var listeners: List<(T) -> Unit> = emptyList()

    fun accept(value: T) {
        emit(value)
    }

    override fun emit(value: T) {
        listeners.forEach { it.invoke(value) }
    }

    override fun observe(callback: (T) -> Unit): Cancellable {
        listeners = listeners + callback
        return cancellableOf {
            listeners = listeners - callback
        }
    }
}
