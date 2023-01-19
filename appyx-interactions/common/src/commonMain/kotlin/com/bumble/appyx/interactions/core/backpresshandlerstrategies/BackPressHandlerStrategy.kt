package com.bumble.appyx.interactions.core.backpresshandlerstrategies

import com.bumble.appyx.interactions.core.BaseTransitionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BackPressHandlerStrategy<NavTarget, State> {

    fun init(navModel: BaseTransitionModel<NavTarget, State>, scope: CoroutineScope)

    val canHandleBackPress: StateFlow<Boolean>

    fun onBackPressed()

}
