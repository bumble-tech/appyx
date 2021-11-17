package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable

interface OnScreenResolver<State> : Parcelable {

    fun isOnScreen(state: State): Boolean
}
