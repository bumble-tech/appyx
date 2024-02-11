package com.bumble.appyx.interactions.core.model

import com.bumble.appyx.combineState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.EmptyCoroutineContext

class CombinedAppyxComponent<InteractionTarget : Any>(
    val appyxComponents: List<AppyxComponent<InteractionTarget, *>>
) : AppyxComponent<InteractionTarget, Any> {

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    override val elements: StateFlow<AppyxComponent.Elements<InteractionTarget>>
        get() = combineState(
            flows = appyxComponents.map { it.elements },
            scope = scope,
        ) { arr ->
            AppyxComponent.Elements(
                onScreen = arr.flatMap { it.onScreen }.toSet(),
                offScreen = arr.flatMap { it.offScreen }.toSet(),
            )
        }

    override fun onAddedToComposition(scope: CoroutineScope) {
        appyxComponents.forEach { it.onAddedToComposition(scope) }
    }

    override fun onRemovedFromComposition() {
        appyxComponents.forEach { it.onRemovedFromComposition() }
    }

    override fun handleBackPress(): Boolean {
        appyxComponents.forEach { appyxComponent ->
            if (appyxComponent.canHandleBackPress().value)
                return appyxComponent.handleBackPress()
        }
        return false
    }

    override fun canHandleBackPress(): StateFlow<Boolean> =
        combineState(appyxComponents.map { it.canHandleBackPress() }, scope) { array ->
            array.any { it }
        }

    override fun destroy() {
        scope.cancel()
        appyxComponents.forEach { it.destroy() }
    }

}

operator fun <InteractionTarget : Any> AppyxComponent<InteractionTarget, *>.plus(
    other: AppyxComponent<InteractionTarget, *>,
): CombinedAppyxComponent<InteractionTarget> {
    val currentModels =
        if (this is CombinedAppyxComponent<InteractionTarget>) appyxComponents else listOf(
            this
        )
    val otherModels =
        if (other is CombinedAppyxComponent<InteractionTarget>) other.appyxComponents else listOf(
            other
        )
    return CombinedAppyxComponent(currentModels + otherModels)
}
