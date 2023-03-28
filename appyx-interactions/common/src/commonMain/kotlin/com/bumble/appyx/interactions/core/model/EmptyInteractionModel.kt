package com.bumble.appyx.interactions.core.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EmptyInteractionModel<InteractionTarget : Any> : InteractionModel<InteractionTarget, Any> {

    override val elements: StateFlow<InteractionModel.Elements<InteractionTarget>>
        get() = MutableStateFlow(InteractionModel.Elements())

    override fun onAddedToComposition(scope: CoroutineScope) = Unit

    override fun onRemovedFromComposition() = Unit

    override fun canHandeBackPress(): StateFlow<Boolean> = MutableStateFlow(false)

    override fun handleBackPress(): Boolean = false

    override fun destroy() = Unit
}
