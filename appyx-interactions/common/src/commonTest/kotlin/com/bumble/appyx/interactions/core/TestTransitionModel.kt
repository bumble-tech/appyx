package com.bumble.appyx.interactions.core

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.TestTransitionModel.State
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel

class TestTransitionModel<NavTarget : Any>(
    initialElements: List<NavTarget>,
) : BaseTransitionModel<NavTarget, State<NavTarget>>() {
    data class State<NavTarget>(val elements: List<NavElement<NavTarget>>)

    override val initialState: State<NavTarget> = State(
        elements = initialElements.map { it.asElement() }
    )

    override fun State<NavTarget>.removeDestroyedElement(navElement: NavElement<NavTarget>) = this

    override fun State<NavTarget>.removeDestroyedElements(): State<NavTarget> = this

    override fun State<NavTarget>.destroyedElements(): Set<NavElement<NavTarget>> = setOf()

    override fun State<NavTarget>.availableElements(): Set<NavElement<NavTarget>> = setOf()
}

@Parcelize
class TestOperation<NavTarget : Any>(
    private val navTarget: @RawValue NavTarget,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<NavTarget>>() {
    override fun createFromState(baseLineState: State<NavTarget>): State<NavTarget> =
        baseLineState

    override fun createTargetState(fromState: State<NavTarget>): State<NavTarget> =
        fromState.copy(elements = fromState.elements + navTarget.asElement())

    override fun isApplicable(state: State<NavTarget>): Boolean = true
}
