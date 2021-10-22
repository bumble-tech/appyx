package com.github.zsoltk.composeribs.core.routing.source.backstack

val <T> List<BackStackElement<T>>.current: BackStackElement<T>?
    get() = this.lastOrNull()
