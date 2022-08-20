package com.bumble.appyx.core.navigation.model.combined

import com.bumble.appyx.core.navigation.NavModel

operator fun <Routing> NavModel<Routing, *>.plus(
    other: NavModel<Routing, *>,
): CombinedNavModel<Routing> {
    val currentModels = if (this is CombinedNavModel<Routing>) navModels else listOf(this)
    val otherModels = if (other is CombinedNavModel<Routing>) other.navModels else listOf(other)
    return CombinedNavModel(currentModels + otherModels)
}
