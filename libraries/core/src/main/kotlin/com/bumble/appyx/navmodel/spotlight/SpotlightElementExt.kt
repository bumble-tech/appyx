package com.bumble.appyx.navmodel.spotlight

import android.os.Parcelable
import com.bumble.appyx.navmodel.spotlight.Spotlight.State.ACTIVE

val <T : Parcelable> SpotlightElements<T>.current: SpotlightElement<T>?
    get() = this.lastOrNull { it.targetState == ACTIVE }

val <T : Parcelable> SpotlightElements<T>.currentIndex: Int
    get() = this.indexOfLast { it.targetState == ACTIVE }
