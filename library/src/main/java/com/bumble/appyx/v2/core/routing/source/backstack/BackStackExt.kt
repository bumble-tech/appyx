package com.bumble.appyx.v2.core.routing.source.backstack

val <T> BackStackElements<T>.current: BackStackElement<T>?
    get() = this.lastOrNull { it.targetState == BackStack.TransitionState.ACTIVE }

val <T> BackStackElements<T>.currentIndex: Int
    get() = this.indexOfLast { it.targetState == BackStack.TransitionState.ACTIVE }
