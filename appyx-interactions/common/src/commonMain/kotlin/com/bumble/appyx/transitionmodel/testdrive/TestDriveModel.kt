package com.bumble.appyx.transitionmodel.testdrive

import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel.State.ElementState.A

class TestDriveModel<NavTarget : Any>(
    val element: NavTarget
) : BaseTransitionModel<NavTarget, TestDriveModel.State<NavTarget>>() {

    data class State<NavTarget>(
        val element: NavElement<NavTarget>,
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

    override fun State<NavTarget>.availableElements(): Set<NavElement<NavTarget>> =
        setOf(element)


    override fun State<NavTarget>.destroyedElements(): Set<NavElement<NavTarget>> =
        emptySet()

    override val initialState: State<NavTarget> =
        State(
            element = element.asElement(),
            elementState = A
        )
}
