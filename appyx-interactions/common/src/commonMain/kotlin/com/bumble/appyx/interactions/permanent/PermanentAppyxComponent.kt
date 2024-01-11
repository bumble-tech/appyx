package com.bumble.appyx.interactions.permanent

import com.bumble.appyx.interactions.core.model.AppyxComponent
import com.bumble.appyx.interactions.core.model.progress.InstantProgressController
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.state.MutableSavedStateMap
import com.bumble.appyx.interactions.permanent.PermanentModel.State
import com.bumble.appyx.mapState
import com.bumble.appyx.utils.multiplatform.SavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PermanentAppyxComponent<InteractionTarget : Any>(
    val model: PermanentModel<InteractionTarget>,
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) : AppyxComponent<InteractionTarget, State<InteractionTarget>> {


    constructor(
        savedStateMap: SavedStateMap?,
        initialTargets: List<InteractionTarget> = emptyList(),
        scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    ) : this(
        model = PermanentModel(
            savedStateMap = savedStateMap,
            initialTargets = initialTargets
        ),
        scope = scope
    )

    private val instant = InstantProgressController(model = model)

    override val elements: StateFlow<AppyxComponent.Elements<InteractionTarget>>
        get() = model.elements.mapState(scope) { elements ->
            AppyxComponent.Elements(onScreen = elements)
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
