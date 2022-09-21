package com.bumble.appyx.navmodel.spotlightadvanced

import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Active

val <T> SpotlightAdvancedElements<T>.current: SpotlightAdvancedElement<T>?
    get() = this.lastOrNull { it.targetState == Active }

val <T> SpotlightAdvancedElements<T>.currentIndex: Int
    get() = this.indexOfLast { it.targetState == Active }
