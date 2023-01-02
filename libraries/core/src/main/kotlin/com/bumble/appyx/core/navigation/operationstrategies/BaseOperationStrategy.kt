package com.bumble.appyx.core.navigation.operationstrategies

import android.os.Parcelable
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.core.navigation.NavModel
import kotlinx.coroutines.CoroutineScope

abstract class BaseOperationStrategy<NavTarget : Parcelable, State : Parcelable> : OperationStrategy<NavTarget, State> {

    protected lateinit var scope: CoroutineScope
    protected lateinit var navModel: NavModel<NavTarget, State>
    protected lateinit var executeOperation: (operation: Operation<NavTarget, State>) -> Unit

    override fun init(
        navModel: NavModel<NavTarget, State>,
        scope: CoroutineScope,
        executeOperation: (operation: Operation<NavTarget, State>) -> Unit
    ) {
        this.scope = scope
        this.navModel = navModel
        this.executeOperation = executeOperation
    }
}
