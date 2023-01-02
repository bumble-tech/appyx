package com.bumble.appyx.core.navigation.operationstrategies

import android.os.Parcelable
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.isTransitioning


class FinishTransitionsOnNewOperation<NavTarget : Parcelable, State : Parcelable> :
    BaseOperationStrategy<NavTarget, State>() {

    override fun accept(operation: Operation<NavTarget, State>) {
        finishUnfinishedTransitions()
        executeOperation(operation)
    }

    private fun finishUnfinishedTransitions() {
        navModel
            .elements
            .value
            .mapNotNull { navElement ->
                if (navElement.isTransitioning) navElement.key else null
            }
            .also { if (it.isNotEmpty()) navModel.onTransitionFinished(it) }
    }
}
