package com.bumble.appyx.transitionmodel.permanent

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.InteractionModel
import com.bumble.appyx.interactions.core.model.progress.InstantProgressController
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.ui.ScreenState
import com.bumble.appyx.transitionmodel.permanent.PermanentModel.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PermanentInteractionModel<InteractionTarget : Any>(
    val model: PermanentModel<InteractionTarget> = PermanentModel(emptyList()),
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : InteractionModel<InteractionTarget, State<InteractionTarget>> {

    private val instant = InstantProgressController(model = model)

    init {
        scope.launch {
            model.elements
                .collect { elements ->
                    _screenState.emit(ScreenState(onScreen = elements))
                    _elements.emit(elements)
                }
        }
    }

    private val _screenState = MutableStateFlow(ScreenState(onScreen = model.elements.value))
    override val screenState: StateFlow<ScreenState<InteractionTarget>>
        get() = _screenState

    private val _elements = MutableStateFlow(model.elements.value)
    override val elements: StateFlow<Set<Element<InteractionTarget>>>
        get() = _elements

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

}
