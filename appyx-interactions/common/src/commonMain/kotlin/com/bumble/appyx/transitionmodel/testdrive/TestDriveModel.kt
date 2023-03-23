package com.bumble.appyx.transitionmodel.testdrive

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.SavedStateMap
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.A

class TestDriveModel<InteractionTarget : Any>(
    val element: InteractionTarget,
    savedStateMap: SavedStateMap? = null,
    key: String = TestDriveModel::class.java.name,
) : BaseTransitionModel<InteractionTarget, TestDriveModel.State<InteractionTarget>>(
    savedStateMap = savedStateMap,
    key = key
) {

    data class State<InteractionTarget>(
        val element: Element<InteractionTarget>,
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

    override fun State<InteractionTarget>.availableElements(): Set<Element<InteractionTarget>> =
        setOf(element)


    override fun State<InteractionTarget>.destroyedElements(): Set<Element<InteractionTarget>> =
        emptySet()

    override val initialState: State<InteractionTarget> =
        State(
            element = element.asElement(),
            elementState = A
        )

    override fun State<InteractionTarget>.removeDestroyedElement(element: Element<InteractionTarget>) =
        this

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> = this
}
