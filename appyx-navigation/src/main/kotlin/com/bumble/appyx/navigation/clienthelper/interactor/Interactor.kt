package com.bumble.appyx.navigation.clienthelper.interactor

import SavesInstanceState
import com.bumble.appyx.navigation.children.ChildAware
import com.bumble.appyx.navigation.children.ChildAwareImpl
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.plugin.NodeAware
import com.bumble.appyx.navigation.plugin.NodeLifecycleAware

open class Interactor<N: Node>(
    private val childAwareImpl: ChildAware<N> = ChildAwareImpl()
) : NodeAware<N>,
    NodeLifecycleAware,
    ChildAware<N> by childAwareImpl,
    SavesInstanceState
