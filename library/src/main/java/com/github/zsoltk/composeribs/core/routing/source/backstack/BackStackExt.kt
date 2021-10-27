package com.github.zsoltk.composeribs.core.routing.source.backstack

val <T> Elements<T>.current: BackStackElement<T>?
    get() = this.lastOrNull { it.targetState == BackStack.TransitionState.ON_SCREEN }

val <T> Elements<T>.currentIndex: Int
    get() = this.indexOfLast { it.targetState == BackStack.TransitionState.ON_SCREEN }
