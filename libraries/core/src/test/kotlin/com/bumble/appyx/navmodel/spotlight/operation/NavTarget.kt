package com.bumble.appyx.navmodel.spotlight.operation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

internal sealed class NavTarget: Parcelable {
    @Parcelize
    object NavTarget1 : NavTarget()
    @Parcelize
    object NavTarget2 : NavTarget()
    @Parcelize
    object NavTarget3 : NavTarget()
}
