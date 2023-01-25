package com.bumble.appyx.transitionmodel.spotlight

import com.bumble.appyx.interactions.core.BaseTransitionModel
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.asElements
import com.bumble.appyx.interactions.core.ui.NavElements
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State

class SpotlightModel<NavTarget : Any>(
    items: List<NavTarget>,
    initialActiveIndex: Float = 0f,
    initialActiveWindow: Float = 1f,
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
        val created: NavElements<NavTarget> = listOf(),
        val standard: NavElements<NavTarget>,
        val destroyed: NavElements<NavTarget> = listOf(),
        val activeIndex: Float,
        val activeWindow: Float
    ) {
        fun hasPrevious(): Boolean =
            activeIndex >= 1

        fun hasNext(): Boolean =
            activeIndex <= standard.lastIndex - 1
    }

    override val initialState: State<NavTarget> =
        State(
            standard = items.asElements(),
            activeIndex = initialActiveIndex,
            activeWindow = initialActiveWindow
        )

    override fun State<NavTarget>.availableElements(): Set<NavElement<NavTarget>> =
        (created + standard).toSet()

    override fun State<NavTarget>.destroyedElements(): Set<NavElement<NavTarget>> =
        destroyed.toSet()
}
