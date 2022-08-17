package com.bumble.appyx.core.navigation.operationstrategies

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.NavModel
import kotlinx.coroutines.CoroutineScope

interface OperationStrategy<Routing, State> {

    fun init(
        navModel: NavModel<Routing, State>,
        scope: CoroutineScope,
        executeOperation: (operation: Operation<Routing, State>) -> Unit
    )

    fun accept(operation: Operation<Routing, State>)
}
