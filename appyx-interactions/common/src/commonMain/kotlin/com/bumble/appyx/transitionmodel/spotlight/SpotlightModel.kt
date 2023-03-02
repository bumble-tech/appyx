package com.bumble.appyx.transitionmodel.spotlight

import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.STANDARD

class SpotlightModel<NavTarget : Any>(
    items: List<NavTarget>,
    initialActiveIndex: Float = 0f
//    savedStateMap: SavedStateMap?,
//    key: String = KEY_NAV_MODEL,
//    backPressHandler: BackPressHandlerStrategy<NavTarget, State> = GoToDefault(
//        initialActiveIndex
//    ),
//    operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
//    screenResolver: OnScreenStateResolver<State> = SpotlightOnScreenResolver
) : BaseTransitionModel<NavTarget, State<NavTarget>>(
//    backPressHandler = backPressHandler,
//    operationStrategy = operationStrategy,
//    screenResolver = screenResolver,
//    finalState = null,
//    savedStateMap = savedStateMap,
//    key = key
) {

    data class State<NavTarget>(
        val positions: List<Position<NavTarget>>,
        val activeIndex: Float
    ) {
        data class Position<NavTarget>(
            val elements: Map<NavElement<NavTarget>, ElementState> = mapOf()
        )

        enum class ElementState {
            CREATED, STANDARD, DESTROYED
        }

        fun hasPrevious(): Boolean =
            activeIndex >= 1

        fun hasNext(): Boolean =
            activeIndex <= positions.lastIndex - 1
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

    override fun State<NavTarget>.removeDestroyedElement(navElement: NavElement<NavTarget>): State<NavTarget> {
        val newPositions = positions.map { position ->
            val newElements = position
                .elements
                .filterNot { mapEntry ->
                    mapEntry.key == navElement && mapEntry.value == DESTROYED
                }

            position.copy(elements = newElements)
        }
        return copy(positions = newPositions)
    }

    override fun State<NavTarget>.removeDestroyedElements(): State<NavTarget>  {
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

    override fun State<NavTarget>.availableElements(): Set<NavElement<NavTarget>> =
        positions
            .flatMap { it.elements.entries }
            .filter { it.value != DESTROYED }
            .map { it.key }
            .toSet()

    override fun State<NavTarget>.destroyedElements(): Set<NavElement<NavTarget>> =
        positions
            .flatMap { it.elements.entries }
            .filter { it.value == DESTROYED }
            .map { it.key }
            .toSet()
}
