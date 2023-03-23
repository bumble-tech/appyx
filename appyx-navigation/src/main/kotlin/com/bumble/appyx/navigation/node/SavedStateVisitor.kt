package com.bumble.appyx.navigation.node

import com.bumble.appyx.interactions.core.InteractionModel
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import com.bumble.appyx.interactions.core.visitor.Visitor
import com.bumble.appyx.navigation.plugin.SavesInstanceState
import com.bumble.appyx.navigation.state.MutableSavedStateMap

class SavedStateVisitor<InteractionTarget : Any, ModelState : Any>(
    private val key: String,
    private val interactionModel: InteractionModel<InteractionTarget, ModelState>
) : Visitor, SavesInstanceState {

    private var state: MutableSavedStateMap? = null

    override fun saveInstanceState(state: MutableSavedStateMap) {
        super.saveInstanceState(state)
        this.state = state
        interactionModel.accept(this)
    }

    override fun <InteractionTarget, ModelState> visit(transitionModel: TransitionModel<InteractionTarget, ModelState>) {
        val output = transitionModel.output
        state?.put(key, output.value)
    }
}
