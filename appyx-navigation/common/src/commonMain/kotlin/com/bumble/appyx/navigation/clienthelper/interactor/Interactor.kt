package com.bumble.appyx.navigation.clienthelper.interactor

import com.bumble.appyx.interactions.core.plugin.SavesInstanceState
import com.bumble.appyx.navigation.children.ChildAware
import com.bumble.appyx.navigation.children.ChildAwareImpl
import com.bumble.appyx.navigation.node.AbstractNode
import com.bumble.appyx.navigation.plugin.NodeAware
import com.bumble.appyx.navigation.plugin.NodeLifecycleAware

open class Interactor<N : AbstractNode>(
    private val childAwareImpl: ChildAware<N> = ChildAwareImpl()
) : NodeAware<N>,
    NodeLifecycleAware,
    ChildAware<N> by childAwareImpl,
    SavesInstanceState
