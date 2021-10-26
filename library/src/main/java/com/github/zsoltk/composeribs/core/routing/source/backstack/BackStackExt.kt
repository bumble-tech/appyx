package com.github.zsoltk.composeribs.core.routing.source.backstack

val <T> Elements<T>.current: BackStackElement<T>?
    get() = this.lastOrNull()
