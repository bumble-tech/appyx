package com.bumble.appyx.navmodel.backstack

import com.bumble.appyx.navmodel.backstack.BackStack.TransitionState.ACTIVE

val <T> BackStackElements<T>.active: BackStackElement<T>?
    get() = lastOrNull { it.targetState == ACTIVE }

val <T : Any> BackStack<T>.active: BackStackElement<T>?
    get() = elements.value.active

val <T> BackStackElements<T>.activeRouting: T?
    get() = active?.key?.routing

val <T : Any> BackStack<T>.activeRouting: T?
    get() = elements.value.activeRouting

val <T> BackStackElements<T>.activeIndex: Int
    get() = indexOfLast { it.targetState == ACTIVE }
