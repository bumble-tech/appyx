package com.bumble.appyx.routingsource.spotlight

val <T> SpotlightElements<T>.current: SpotlightElement<T>?
    get() = this.lastOrNull { it.targetState == Spotlight.TransitionState.ACTIVE }

val <T> SpotlightElements<T>.currentIndex: Int
    get() = this.indexOfLast { it.targetState == Spotlight.TransitionState.ACTIVE }
