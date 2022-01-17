package com.github.zsoltk.composeribs.core.clienthelper.interactor

import com.github.zsoltk.composeribs.core.children.ChildAware
import com.github.zsoltk.composeribs.core.children.ChildAwareImpl
import com.github.zsoltk.composeribs.core.plugin.NodeAware
import com.github.zsoltk.composeribs.core.plugin.NodeLifecycleAware
import com.github.zsoltk.composeribs.core.plugin.SavesInstanceState
import com.github.zsoltk.composeribs.core.plugin.UpNavigationHandler

abstract class Interactor(private val childAwareImpl: ChildAware = ChildAwareImpl()) : NodeAware,
    NodeLifecycleAware,
    ChildAware by childAwareImpl,
    UpNavigationHandler,
    SavesInstanceState
