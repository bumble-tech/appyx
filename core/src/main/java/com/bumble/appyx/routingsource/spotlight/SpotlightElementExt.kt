package com.bumble.appyx.routingsource.spotlight

import com.bumble.appyx.routingsource.spotlight.Spotlight.TransitionState.ACTIVE

val <T> SpotlightElements<T>.current: SpotlightElement<T>?
    get() = this.lastOrNull { it.targetState == ACTIVE }

val <T> SpotlightElements<T>.currentIndex: Int
    get() = this.indexOfLast { it.targetState == ACTIVE }
