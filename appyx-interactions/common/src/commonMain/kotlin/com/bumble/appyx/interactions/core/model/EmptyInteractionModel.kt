package com.bumble.appyx.interactions.core.model

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.ui.ScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EmptyInteractionModel<InteractionTarget : Any> : InteractionModel<InteractionTarget, Any> {

    override val elements: StateFlow<Set<Element<InteractionTarget>>>
        get() = MutableStateFlow(emptySet())

    override val screenState: StateFlow<ScreenState<InteractionTarget>>
        get() = MutableStateFlow(ScreenState())

    override fun onAddedToComposition(scope: CoroutineScope) = Unit

    override fun onRemovedFromComposition() = Unit

    override fun canHandeBackPress(): StateFlow<Boolean> = MutableStateFlow(false)

    override fun handleBackPress(): Boolean = false

    override fun destroy() = Unit
}
