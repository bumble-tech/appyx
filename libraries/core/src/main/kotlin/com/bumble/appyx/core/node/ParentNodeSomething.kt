package com.bumble.appyx.core.node

import androidx.compose.runtime.Stable
import com.bumble.appyx.core.children.ChildEntry
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.NavModel

// TODO: Naming!
@Stable
interface ParentNodeSomething<NavTarget : Any> {

    val navModel: NavModel<NavTarget, *>

    fun childOrCreate(navKey: NavKey<NavTarget>): ChildEntry.Initialized<NavTarget>

}