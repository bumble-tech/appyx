package com.bumble.appyx.navmodel2.backstack

import com.bumble.appyx.core.navigation2.NavElement
import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.navmodel2.backstack.BackStack.State
import com.bumble.appyx.navmodel2.backstack.BackStack.State.ACTIVE

typealias BackStackElement<T> = NavElement<T, State>

typealias BackStackElements<NavTarget> = NavElements<NavTarget, State>


val <T> BackStackElements<T>.active: BackStackElement<T>?
    get() = lastOrNull { it.state == ACTIVE }

val <T : Any> BackStack<T>.active: BackStackElement<T>?
    get() = segments.value.navTransition.targetState.active

val <T> BackStackElements<T>.activeElement: T?
    get() = active?.key?.navTarget

val <T : Any> BackStack<T>.activeElement: T?
    get() = segments.value.navTransition.targetState.activeElement

val <T> BackStackElements<T>.activeIndex: Int
    get() = indexOfLast { it.state == ACTIVE }
