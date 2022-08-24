package com.bumble.appyx.core.clienthelper.interactor

import com.bumble.appyx.core.children.ChildAware
import com.bumble.appyx.core.children.ChildAwareImpl
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.NodeAware
import com.bumble.appyx.core.plugin.NodeLifecycleAware
import com.bumble.appyx.core.plugin.SavesInstanceState

abstract class Interactor<N: Node>(
    private val childAwareImpl: ChildAware<N> = ChildAwareImpl()
) : NodeAware<N>,
    NodeLifecycleAware,
    ChildAware<N> by childAwareImpl,
    SavesInstanceState
