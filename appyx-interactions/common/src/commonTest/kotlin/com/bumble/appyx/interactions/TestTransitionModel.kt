package com.bumble.appyx.interactions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.bumble.appyx.interactions.TestTransitionModel.State
import com.bumble.appyx.interactions.model.transition.BaseOperation
import com.bumble.appyx.interactions.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.interactions.gesture.Gesture
import com.bumble.appyx.interactions.gesture.GestureFactory
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue

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
    override fun createGesture(
        state: State<InteractionTarget>,
        delta: Offset,
        density: Density
    ): Gesture<InteractionTarget, State<InteractionTarget>> =
        Gesture(
            operation = TestOperation(target),
            completeAt = Offset(100f, 100f)
        )
}

@Parcelize
class TestOperation<InteractionTarget : Any>(
    private val interactionTarget: @RawValue InteractionTarget,
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<State<InteractionTarget>>() {
    override fun createFromState(baseLineState: State<InteractionTarget>): State<InteractionTarget> =
        baseLineState

    override fun createTargetState(fromState: State<InteractionTarget>): State<InteractionTarget> =
        fromState.copy(elements = fromState.elements + interactionTarget.asElement())

    override fun isApplicable(state: State<InteractionTarget>): Boolean = true
}
