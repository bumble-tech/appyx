package com.bumble.appyx.transitionmodel.backstack

import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State
import com.bumble.appyx.transitionmodel.backstack.BackStackModel.State.ACTIVE

typealias BackStackElement<T> = NavElement<T, State>

typealias BackStackElements<NavTarget> = NavElements<NavTarget, State>


val <T> BackStackElements<T>.active: BackStackElement<T>?
    get() = lastOrNull { it.state == ACTIVE }

val <T : Any> BackStackModel<T>.active: BackStackElement<T>?
    get() = segments.value.navTransition.targetState.active

val <T> BackStackElements<T>.activeElement: T?
    get() = active?.key?.navTarget

val <T : Any> BackStackModel<T>.activeElement: T?
    get() = segments.value.navTransition.targetState.activeElement

val <T> BackStackElements<T>.activeIndex: Int
    get() = indexOfLast { it.state == ACTIVE }
