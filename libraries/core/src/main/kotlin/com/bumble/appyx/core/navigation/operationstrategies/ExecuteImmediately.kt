package com.bumble.appyx.core.navigation.operationstrategies

import android.os.Parcelable
import com.bumble.appyx.core.navigation.Operation

class ExecuteImmediately<NavTarget : Parcelable, State : Parcelable> : BaseOperationStrategy<NavTarget, State>() {

    override fun accept(operation: Operation<NavTarget, State>) {
        executeOperation(operation)
    }
}
