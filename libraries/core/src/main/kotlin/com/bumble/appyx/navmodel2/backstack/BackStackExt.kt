package com.bumble.appyx.navmodel2.backstack

import com.bumble.appyx.navmodel2.backstack.BackStack.State.ACTIVE

val <T> BackStackElements<T>.active: BackStackElement<T>?
    get() = lastOrNull { it.targetState == ACTIVE }

val <T : Any> BackStack<T>.active: BackStackElement<T>?
    get() = elements.value.targetState.active

val <T> BackStackElements<T>.activeElement: T?
    get() = active?.key?.navTarget

val <T : Any> BackStack<T>.activeElement: T?
    get() = elements.value.targetState.activeElement

val <T> BackStackElements<T>.activeIndex: Int
    get() = indexOfLast { it.targetState == ACTIVE }
