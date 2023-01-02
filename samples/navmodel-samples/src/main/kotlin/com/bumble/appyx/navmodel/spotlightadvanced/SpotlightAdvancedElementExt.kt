package com.bumble.appyx.navmodel.spotlightadvanced

import android.os.Parcelable
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Active

val <T : Parcelable> SpotlightAdvancedElements<T>.current: SpotlightAdvancedElement<T>?
    get() = this.lastOrNull { it.targetState == Active }

val <T : Parcelable> SpotlightAdvancedElements<T>.currentIndex: Int
    get() = this.indexOfLast { it.targetState == Active }
