package com.bumble.appyx.components.spotlight

import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.components.spotlight.SpotlightModel.State.ElementState.STANDARD
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.model.asElement
import com.bumble.appyx.interactions.model.transition.BaseTransitionModel
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue
import com.bumble.appyx.utils.multiplatform.SavedStateMap

class SpotlightModel<NavTarget : Any>(
    items: List<NavTarget>,
    initialActiveIndex: Float = 0f,
    savedStateMap: SavedStateMap?,
) : BaseTransitionModel<NavTarget, State<NavTarget>>(
    savedStateMap = savedStateMap
) {

    @Parcelize
    data class State<NavTarget>(
        val positions: @RawValue List<Position<NavTarget>>,
        val activeIndex: Float
    ) : Parcelable {

        @Parcelize
        data class Position<NavTarget>(
            val elements: Map<Element<NavTarget>, ElementState> = mapOf()
        ) : Parcelable

        enum class ElementState {
            CREATED, STANDARD, DESTROYED
        }

        fun hasPrevious(): Boolean =
            activeIndex >= 1

        fun hasNext(): Boolean =
            activeIndex <= positions.lastIndex - 1

        val activeElement: NavTarget? =
            positions.getOrNull(activeIndex.toInt())?.elements?.firstNotNullOf { it.key.interactionTarget }
    }

    override val initialState: State<NavTarget> =
        State(
            positions = items.map {
                State.Position(
                    elements = mapOf(it.asElement() to STANDARD)
                )
            },
            activeIndex = initialActiveIndex
        )

    val currentState: State<NavTarget>
        get() = output.value.currentTargetState

    override fun State<NavTarget>.removeDestroyedElement(
        element: Element<NavTarget>
    ): State<NavTarget> {
        val newPositions = positions.map { position ->
            val newElements = position
                .elements
                .filterNot { mapEntry ->
                    mapEntry.key == element && mapEntry.value == DESTROYED
                }

            position.copy(elements = newElements)
        }
        return copy(positions = newPositions)
    }

    override fun State<NavTarget>.removeDestroyedElements(): State<NavTarget> {
        val newPositions = positions.map { position ->
            val newElements = position
                .elements
                .filterNot { mapEntry ->
                    mapEntry.value == DESTROYED
                }

            position.copy(elements = newElements)
        }
        return copy(positions = newPositions)
    }

    override fun State<NavTarget>.availableElements(): Set<Element<NavTarget>> =
        positions
            .flatMap { it.elements.entries }
            .filter { it.value != DESTROYED }
            .map { it.key }
            .toSet()

    override fun State<NavTarget>.destroyedElements(): Set<Element<NavTarget>> =
        positions
            .flatMap { it.elements.entries }
            .filter { it.value == DESTROYED }
            .map { it.key }
            .toSet()
}
