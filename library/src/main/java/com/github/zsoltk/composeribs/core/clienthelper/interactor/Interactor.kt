package com.github.zsoltk.composeribs.core.clienthelper.interactor

import com.github.zsoltk.composeribs.core.children.ChildAware
import com.github.zsoltk.composeribs.core.children.ChildAwareImpl
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.plugin.NodeAware
import com.github.zsoltk.composeribs.core.plugin.NodeLifecycleAware
import com.github.zsoltk.composeribs.core.plugin.SavesInstanceState

abstract class Interactor<N: Node>(
    private val childAwareImpl: ChildAware<N> = ChildAwareImpl()
) : NodeAware<N>,
    NodeLifecycleAware,
    ChildAware<N> by childAwareImpl,
    SavesInstanceState
