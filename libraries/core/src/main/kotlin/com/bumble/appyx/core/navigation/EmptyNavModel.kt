package com.bumble.appyx.core.navigation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver

/**
 * An implementation of a NavModel that won't add any children.
 * This is potentially useful if your ParentNode only uses
 * [com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel]
 */
class EmptyNavModel<NavTarget: Parcelable, State: Parcelable> : BaseNavModel<NavTarget, State>(
    savedStateMap = null,
    finalState = null,
    screenResolver = OnScreenStateResolver { true }
) {
    override val initialElements: NavElements<NavTarget, State>
        get() = emptyList()
}
