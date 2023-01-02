package com.bumble.appyx.navmodel.backstack

import android.os.Parcelable
import com.bumble.appyx.navmodel.backstack.BackStack.State.ACTIVE

val <T : Parcelable> BackStackElements<T>.active: BackStackElement<T>?
    get() = lastOrNull { it.targetState == ACTIVE }

val <T : Parcelable> BackStack<T>.active: BackStackElement<T>?
    get() = elements.value.active

val <T : Parcelable> BackStackElements<T>.activeElement: T?
    get() = active?.key?.navTarget

val <T : Parcelable> BackStack<T>.activeElement: T?
    get() = elements.value.activeElement

val <T : Parcelable> BackStackElements<T>.activeIndex: Int
    get() = indexOfLast { it.targetState == ACTIVE }
