package com.bumble.appyx.core.navigation.backpresshandlerstrategies

import com.bumble.appyx.core.navigation.BaseNavModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class BaseBackPressHandlerStrategy<NavTarget, State>
    : BackPressHandlerStrategy<NavTarget, State> {

    protected lateinit var scope: CoroutineScope
    protected lateinit var navModel: BaseNavModel<NavTarget, State>

    override fun init(
        navModel: BaseNavModel<NavTarget, State>,
        scope: CoroutineScope
    ) {
        this.scope = scope
        this.navModel = navModel
    }

    protected abstract val canHandleBackPressFlow: Flow<Boolean>

    override val canHandleBackPress: StateFlow<Boolean> by lazy(LazyThreadSafetyMode.NONE) {
        canHandleBackPressFlow.stateIn(scope, SharingStarted.Eagerly, false)
    }
}
