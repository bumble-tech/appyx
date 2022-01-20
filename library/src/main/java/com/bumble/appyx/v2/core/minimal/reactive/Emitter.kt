package com.bumble.appyx.v2.core.minimal.reactive

interface Emitter<in T> {
    fun emit(value: T)
}
