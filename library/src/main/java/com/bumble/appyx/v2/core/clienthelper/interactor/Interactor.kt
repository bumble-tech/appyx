package com.bumble.appyx.v2.core.clienthelper.interactor

import com.bumble.appyx.v2.core.children.ChildAware
import com.bumble.appyx.v2.core.children.ChildAwareImpl
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.plugin.NodeAware
import com.bumble.appyx.v2.core.plugin.NodeLifecycleAware
import com.bumble.appyx.v2.core.plugin.SavesInstanceState

abstract class Interactor<N: Node>(
    private val childAwareImpl: ChildAware<N> = ChildAwareImpl()
) : NodeAware<N>,
    NodeLifecycleAware,
    ChildAware<N> by childAwareImpl,
    SavesInstanceState
