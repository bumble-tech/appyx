package com.bumble.appyx.interactions.core

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.TestTransitionModel.State
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory

class TestTransitionModel<InteractionTarget : Any>(
    initialElements: List<InteractionTarget>,
) : BaseTransitionModel<InteractionTarget, State<InteractionTarget>>(
    savedStateMap = null
) {
    @Parcelize
    data class State<InteractionTarget>(
        val elements: List<Element<InteractionTarget>>
    ) : Parcelable

    override val initialState: State<InteractionTarget> = State(
        elements = initialElements.map { it.asElement() }
    )

    override fun State<InteractionTarget>.removeDestroyedElement(element: Element<InteractionTarget>) =
        this

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> = this

    override fun State<InteractionTarget>.destroyedElements(): Set<Element<InteractionTarget>> =
        setOf()

    override fun State<InteractionTarget>.availableElements(): Set<Element<InteractionTarget>> =
        setOf()
}

class TestGestures<InteractionTarget : Any>(
    private val target: InteractionTarget,
) : GestureFactory<InteractionTarget, State<InteractionTarget>> {
    override fun createGesture(delta: Offset, density: Density): Gesture<InteractionTarget, State<InteractionTarget>> =
        Gesture(
            operation = TestOperation(target),
            dragToProgress = { _ -> 0f },
            partial = { offset, _ -> offset }
        )
}

@Parcelize
class TestOperation<InteractionTarget : Any>(
    private val interactionTarget: @RawValue InteractionTarget,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<InteractionTarget>>() {
    override fun createFromState(baseLineState: State<InteractionTarget>): State<InteractionTarget> =
        baseLineState

    override fun createTargetState(fromState: State<InteractionTarget>): State<InteractionTarget> =
        fromState.copy(elements = fromState.elements + interactionTarget.asElement())

    override fun isApplicable(state: State<InteractionTarget>): Boolean = true
}
