package com.bumble.appyx.core.navigation.backpresshandlerstrategies

import com.bumble.appyx.core.navigation.BaseNavModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BackPressHandlerStrategy<Routing, State> {

    fun init(navModel: BaseNavModel<Routing, State>, scope: CoroutineScope)

    val canHandleBackPress: StateFlow<Boolean>

    fun onBackPressed()

}
