package com.bumble.appyx.interactions.permanent

import com.bumble.appyx.interactions.core.model.InteractionModel
import com.bumble.appyx.interactions.core.model.progress.InstantProgressController
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.state.MutableSavedStateMap
import com.bumble.appyx.interactions.permanent.PermanentModel.State
import com.bumble.appyx.mapState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PermanentInteractionModel<InteractionTarget : Any>(
    val model: PermanentModel<InteractionTarget>,
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) : InteractionModel<InteractionTarget, State<InteractionTarget>> {

    private val instant = InstantProgressController(model = model)

    override val elements: StateFlow<InteractionModel.Elements<InteractionTarget>>
        get() = model.elements.mapState(scope) { elements ->
            InteractionModel.Elements(onScreen = elements)
        }

    override fun onAddedToComposition(scope: CoroutineScope) = Unit

    override fun onRemovedFromComposition() = Unit

    fun operation(operation: Operation<State<InteractionTarget>>) {
        instant.operation(operation)
    }

    override fun canHandeBackPress(): StateFlow<Boolean> = MutableStateFlow(false)

    override fun handleBackPress(): Boolean = false

    override fun destroy() {
        scope.cancel()
    }

    override fun saveInstanceState(state: MutableSavedStateMap) {
        model.saveInstanceState(state)
    }

}
