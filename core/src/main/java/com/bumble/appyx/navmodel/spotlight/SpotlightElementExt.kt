package com.bumble.appyx.navmodel.spotlight

import com.bumble.appyx.navmodel.spotlight.Spotlight.TransitionState.ACTIVE

val <T> SpotlightElements<T>.current: SpotlightElement<T>?
    get() = this.lastOrNull { it.targetState == ACTIVE }

val <T> SpotlightElements<T>.currentIndex: Int
    get() = this.indexOfLast { it.targetState == ACTIVE }
