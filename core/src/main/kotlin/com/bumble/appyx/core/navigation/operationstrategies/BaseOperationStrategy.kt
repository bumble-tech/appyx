package com.bumble.appyx.core.navigation.operationstrategies

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.NavModel
import kotlinx.coroutines.CoroutineScope

abstract class BaseOperationStrategy<Routing, State> : OperationStrategy<Routing, State> {

    protected lateinit var scope: CoroutineScope
    protected lateinit var navModel: NavModel<Routing, State>
    protected lateinit var executeOperation: (operation: Operation<Routing, State>) -> Unit

    override fun init(
        navModel: NavModel<Routing, State>,
        scope: CoroutineScope,
        executeOperation: (operation: Operation<Routing, State>) -> Unit
    ) {
        this.scope = scope
        this.navModel = navModel
        this.executeOperation = executeOperation
    }
}
