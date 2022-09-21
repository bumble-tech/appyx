package com.bumble.appyx.navmodel.backstack.operation

internal sealed class NavTarget {
    object NavTarget1 : NavTarget()
    object NavTarget2 : NavTarget()
    object NavTarget3 : NavTarget()
    data class NavTarget4(val dummy: String) : NavTarget()
}
