package com.bumble.appyx.navmodel.backstack.operation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal sealed class NavTarget: Parcelable {
    object NavTarget1 : NavTarget()
    object NavTarget2 : NavTarget()
    object NavTarget3 : NavTarget()
    data class NavTarget4(val dummy: String) : NavTarget()
}
