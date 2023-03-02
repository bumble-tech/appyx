package com.bumble.appyx.transitionmodel.testdrive

import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.A

class TestDriveModel<InteractionTarget : Any>(
    val element: InteractionTarget
) : BaseTransitionModel<InteractionTarget, TestDriveModel.State<InteractionTarget>>() {

    data class State<InteractionTarget>(
        val element: NavElement<InteractionTarget>,
        val elementState: ElementState
    ) {
        enum class ElementState {
            A, B, C, D;

            fun next(): ElementState =
                when (this) {
                    A -> B
                    B -> C
                    C -> D
                    D -> A
                }
        }
    }

    override fun State<InteractionTarget>.availableElements(): Set<NavElement<InteractionTarget>> =
        setOf(element)


    override fun State<InteractionTarget>.destroyedElements(): Set<NavElement<InteractionTarget>> =
        emptySet()

    override val initialState: State<InteractionTarget> =
        State(
            element = element.asElement(),
            elementState = A
        )

    override fun State<InteractionTarget>.removeDestroyedElement(navElement: NavElement<InteractionTarget>) = this

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> = this
}
