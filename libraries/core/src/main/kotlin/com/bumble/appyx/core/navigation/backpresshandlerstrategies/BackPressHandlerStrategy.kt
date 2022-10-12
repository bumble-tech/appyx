package com.bumble.appyx.core.navigation.backpresshandlerstrategies

import com.bumble.appyx.core.navigation.BaseNavModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BackPressHandlerStrategy<NavTarget, State> {

    fun init(navModel: BaseNavModel<NavTarget, State>, scope: CoroutineScope)

    val canHandleBackPress: StateFlow<Boolean>

    fun onBackPressed()

}
