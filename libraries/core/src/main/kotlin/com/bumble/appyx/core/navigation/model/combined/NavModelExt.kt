package com.bumble.appyx.core.navigation.model.combined

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavModel

operator fun <NavTarget : Parcelable> NavModel<NavTarget, *>.plus(
    other: NavModel<NavTarget, *>,
): CombinedNavModel<NavTarget> {
    val currentModels = if (this is CombinedNavModel<NavTarget>) navModels else listOf(this)
    val otherModels = if (other is CombinedNavModel<NavTarget>) other.navModels else listOf(other)
    return CombinedNavModel(currentModels + otherModels)
}
