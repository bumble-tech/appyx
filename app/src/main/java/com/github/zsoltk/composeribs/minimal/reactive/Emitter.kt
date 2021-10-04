package com.github.zsoltk.composeribs.minimal.reactive

interface Emitter<in T> {
    fun emit(value: T)
}
