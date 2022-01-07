package com.github.zsoltk.composeribs.core.minimal.reactive

interface Emitter<in T> {
    fun emit(value: T)
}
