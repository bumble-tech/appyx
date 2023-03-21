package com.bumble.appyx.interactions.core.model

import com.bumble.appyx.combineState
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.ui.ScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.EmptyCoroutineContext

class CombinedInteractionModel<InteractionTarget : Any>(
    val interactionModels: List<InteractionModel<InteractionTarget, *>>
) : InteractionModel<InteractionTarget, Any> {

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    override val elements: StateFlow<Set<Element<InteractionTarget>>>
        get() = combineState(interactionModels.map { it.elements }, scope) { arr ->
            arr.flatMap { it }.toSet()
        }

    override val screenState: StateFlow<ScreenState<InteractionTarget>>
        get() = combineState(
            flows = interactionModels.map { it.screenState },
            scope = scope,
        ) { arr ->
            ScreenState(
                onScreen = arr.flatMap { it.onScreen }.toSet(),
                offScreen = arr.flatMap { it.offScreen }.toSet(),
            )
        }

    override fun onAddedToComposition(scope: CoroutineScope) {
        interactionModels.forEach { it.onAddedToComposition(scope) }
    }

    override fun onRemovedFromComposition() {
        interactionModels.forEach { it.onRemovedFromComposition() }
    }

    override fun handleBackPress(): Boolean {
        interactionModels.forEach { interactionModel ->
            if (interactionModel.canHandeBackPress().value)
                return interactionModel.handleBackPress()
        }
        return false
    }

    override fun canHandeBackPress(): StateFlow<Boolean> =
        combineState(interactionModels.map { it.canHandeBackPress() }, scope) { array ->
            array.any { it }
        }

    override fun destroy() {
        scope.cancel()
        interactionModels.forEach { it.destroy() }
    }

}

operator fun <InteractionTarget : Any> InteractionModel<InteractionTarget, *>.plus(
    other: InteractionModel<InteractionTarget, *>,
): CombinedInteractionModel<InteractionTarget> {
    val currentModels =
        if (this is CombinedInteractionModel<InteractionTarget>) interactionModels else listOf(
            this
        )
    val otherModels =
        if (other is CombinedInteractionModel<InteractionTarget>) other.interactionModels else listOf(
            other
        )
    return CombinedInteractionModel(currentModels + otherModels)
}
