package com.bumble.appyx.interactions.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EmptyAppyxComponent<InteractionTarget : Any> :
    AppyxComponent<InteractionTarget, Any> {

    override val elements: StateFlow<AppyxComponent.Elements<InteractionTarget>>
        get() = MutableStateFlow(AppyxComponent.Elements())

    override fun onAddedToComposition(scope: CoroutineScope) = Unit

    override fun onRemovedFromComposition() = Unit

    override fun canHandleBackPress(): StateFlow<Boolean> = MutableStateFlow(false)

    override fun handleBackPress(): Boolean = false

    override fun destroy() = Unit
}
