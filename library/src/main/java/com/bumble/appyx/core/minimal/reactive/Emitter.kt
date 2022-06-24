package com.bumble.appyx.core.minimal.reactive

interface Emitter<in T> {
    fun emit(value: T)
}
