package com.bumble.appyx.interactions.core.model

import androidx.compose.runtime.Stable
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.plugin.SavesInstanceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

@Stable
interface AppyxComponent<InteractionTarget : Any, ModelState : Any> : SavesInstanceState {

    class Elements<InteractionTarget> private constructor(
        val onScreen: Set<Element<InteractionTarget>>,
        val offScreen: Set<Element<InteractionTarget>>,
        val all: Set<Element<InteractionTarget>>
    ) {
        constructor(
            onScreen: Set<Element<InteractionTarget>> = emptySet(),
            offScreen: Set<Element<InteractionTarget>> = emptySet()
        ) : this(onScreen = onScreen, offScreen = offScreen, all = onScreen + offScreen)
    }

    val elements: StateFlow<Elements<InteractionTarget>>

    fun onAddedToComposition(scope: CoroutineScope)

    fun onRemovedFromComposition()

    fun canHandeBackPress(): StateFlow<Boolean>

    fun handleBackPress(): Boolean

    fun destroy()
}
