package com.bumble.appyx.navmodel2.spotlight

import com.bumble.appyx.core.navigation2.NavElement
import com.bumble.appyx.core.navigation2.NavElements
import com.bumble.appyx.navmodel2.spotlight.Spotlight.State.ACTIVE
import kotlinx.coroutines.flow.map

typealias SpotlightElement<NavTarget> = NavElement<NavTarget, Spotlight.State>

typealias SpotlightElements<NavTarget> = NavElements<NavTarget, Spotlight.State>

val <NavTarget> SpotlightElements<NavTarget>.current: SpotlightElement<NavTarget>?
    get() = this.lastOrNull { it.state == ACTIVE }

val <NavTarget> SpotlightElements<NavTarget>.currentIndex: Int
    get() = this.indexOfLast { it.state == ACTIVE }


fun <NavTarget : Any> Spotlight<NavTarget>.hasNext() =
    segments.map { value ->
        value.navTransition.targetState.lastIndex !=
        segments.value.navTransition.targetState.currentIndex
    }

fun <NavTarget : Any> Spotlight<NavTarget>.hasPrevious() =
    segments.map { value -> value.navTransition.targetState.currentIndex != 0 }

fun <NavTarget : Any> Spotlight<NavTarget>.activeIndex() =
    segments.map { value -> value.navTransition.targetState.currentIndex }

fun <T : Any> Spotlight<T>.elementsCount() =
    segments.value.navTransition.targetState.size
