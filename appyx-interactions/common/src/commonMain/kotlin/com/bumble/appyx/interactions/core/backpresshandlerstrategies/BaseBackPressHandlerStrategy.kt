package com.bumble.appyx.interactions.core.backpresshandlerstrategies

import com.bumble.appyx.interactions.core.BaseTransitionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class BaseBackPressHandlerStrategy<NavTarget, State>
    : BackPressHandlerStrategy<NavTarget, State> {

    protected lateinit var scope: CoroutineScope
    protected lateinit var navModel: BaseTransitionModel<NavTarget, State>

    override fun init(
        navModel: BaseTransitionModel<NavTarget, State>,
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
