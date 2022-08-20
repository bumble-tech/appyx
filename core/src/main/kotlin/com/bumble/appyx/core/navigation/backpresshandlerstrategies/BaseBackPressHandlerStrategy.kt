package com.bumble.appyx.core.navigation.backpresshandlerstrategies

import com.bumble.appyx.core.navigation.BaseNavModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class BaseBackPressHandlerStrategy<Routing, TransitionState>
    : BackPressHandlerStrategy<Routing, TransitionState> {

    protected lateinit var scope: CoroutineScope
    protected lateinit var navModel: BaseNavModel<Routing, TransitionState>

    override fun init(
        navModel: BaseNavModel<Routing, TransitionState>,
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
