package com.bumble.appyx.navigation.node.cakes.component.spotlighthero

import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.State
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.State.ElementState.DESTROYED
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.SpotlightHeroModel.State.ElementState.STANDARD
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.state.SavedStateMap
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue

class SpotlightHeroModel<InteractionTarget : Any>(
    items: List<InteractionTarget>,
    initialMode: Mode = Mode.LIST,
    initialActiveIndex: Float = 0f,
    savedStateMap: SavedStateMap?,
) : BaseTransitionModel<InteractionTarget, State<InteractionTarget>>(
    savedStateMap = savedStateMap
) {
    enum class Mode {
        LIST, HERO
    }

    @Parcelize
    data class State<InteractionTarget>(
        val mode: Mode = Mode.LIST,
        val positions: @RawValue List<Position<InteractionTarget>>,
        val activeIndex: Float
    ) : Parcelable {

        @Parcelize
        data class Position<InteractionTarget>(
            val elements: Map<Element<InteractionTarget>, ElementState> = mapOf()
        ) : Parcelable

        enum class ElementState {
            CREATED, STANDARD, SELECTED, DESTROYED
        }

        fun hasPrevious(): Boolean =
            activeIndex >= 1

        fun hasNext(): Boolean =
            activeIndex <= positions.lastIndex - 1
    }

    override val initialState: State<InteractionTarget> =
        State(
            mode = initialMode,
            positions = items.map {
                State.Position(
                    elements = mapOf(it.asElement() to STANDARD)
                )
            },
            activeIndex = initialActiveIndex
        )

    override fun State<InteractionTarget>.removeDestroyedElement(
        element: Element<InteractionTarget>
    ): State<InteractionTarget> {
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

    override fun State<InteractionTarget>.removeDestroyedElements(): State<InteractionTarget> {
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

    override fun State<InteractionTarget>.availableElements(): Set<Element<InteractionTarget>> =
        positions
            .flatMap { it.elements.entries }
            .filter { it.value != DESTROYED }
            .map { it.key }
            .toSet()

    override fun State<InteractionTarget>.destroyedElements(): Set<Element<InteractionTarget>> =
        positions
            .flatMap { it.elements.entries }
            .filter { it.value == DESTROYED }
            .map { it.key }
            .toSet()
}