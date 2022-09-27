package com.bumble.appyx.core.navigation.operationstrategies

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.isTransitioning

class IgnoreIfThereAreUnfinishedTransitions<NavTarget, State> : BaseOperationStrategy<NavTarget, State>() {

    override fun accept(operation: Operation<NavTarget, State>) {
        if (hasNoUnfinishedTransitions()) {
            executeOperation(operation)
        }
    }

    private fun hasNoUnfinishedTransitions(): Boolean =
        navModel.elements.value
            .none { it.isTransitioning }
}
