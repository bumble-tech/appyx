package com.bumble.appyx.components.internal.testdrive

import com.bumble.appyx.components.internal.testdrive.TestDriveModel.State.ElementState.A
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.state.SavedStateMap
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class TestDriveModel<InteractionTarget : Any>(
    val element: InteractionTarget,
    savedStateMap: SavedStateMap?,
) : BaseTransitionModel<InteractionTarget, TestDriveModel.State<InteractionTarget>>(
    savedStateMap = savedStateMap
) {

    @Parcelize
    data class State<InteractionTarget>(
        val element: Element<InteractionTarget>,
        val elementState: ElementState
    ) : Parcelable {
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
