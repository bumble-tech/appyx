package com.bumble.appyx.core.navigation.operationstrategies

import android.os.Parcelable
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.NavModel
import kotlinx.coroutines.CoroutineScope

interface OperationStrategy<NavTarget : Parcelable, State : Parcelable> {

    fun init(
        navModel: NavModel<NavTarget, State>,
        scope: CoroutineScope,
        executeOperation: (operation: Operation<NavTarget, State>) -> Unit
    )

    fun accept(operation: Operation<NavTarget, State>)
}
