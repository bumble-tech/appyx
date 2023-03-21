package com.bumble.appyx.interactions.core.model

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.ui.ScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface InteractionModel<InteractionTarget : Any, ModelState : Any> {

    val elements: StateFlow<Set<Element<InteractionTarget>>>

    val screenState: StateFlow<ScreenState<InteractionTarget>>

    fun onAddedToComposition(scope: CoroutineScope)

    fun onRemovedFromComposition()

    fun canHandeBackPress(): StateFlow<Boolean>

    fun handleBackPress(): Boolean

    fun destroy()
}
