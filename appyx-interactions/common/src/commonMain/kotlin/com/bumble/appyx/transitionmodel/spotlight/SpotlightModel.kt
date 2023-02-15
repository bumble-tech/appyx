package com.bumble.appyx.transitionmodel.spotlight

import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.STANDARD

class SpotlightModel<NavTarget : Any>(
    items: List<NavTarget>,
    initialActiveIndex: Float = 0f,
    // indicates how many more elements to the right and left from the centred one are visible
    initialExtraWindow: Int = 0,
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
        val activeIndex: Float,
        val extraWindow: Int
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
            activeIndex = initialActiveIndex,
            extraWindow = initialExtraWindow
        )

    // TODO support removing destroyed elements
    override fun State<NavTarget>.removeDestroyedElements(): State<NavTarget> = this

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
