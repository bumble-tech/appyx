package com.bumble.appyx.transitionmodel.spotlight.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.NavElement
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavKey
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.inputsource.InputSource
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ACTIVE
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.INACTIVE_AFTER
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.INACTIVE_BEFORE

@Parcelize
class UpdateElements<NavTarget : Any>(
    private val elements: @RawValue List<NavTarget>,
    private val initialActiveIndex: Int? = null,
) : Operation<NavTarget, State> {

    override fun isApplicable(elements: NavElements<NavTarget, State>) = true

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        if (initialActiveIndex != null) {
            require(initialActiveIndex in this.elements.indices) {
                "Initial active index $initialActiveIndex " +
                    "is out of bounds of provided list of items: ${this.elements.indices}"
            }
        }
        val state = if (initialActiveIndex == null) {
            val currentActiveElement = elements.find { it.state == ACTIVE }

            // if current navTarget exists in the new list of items and initialActiveIndex is null
            // then keep existing navTarget active
            if (this.elements.contains(currentActiveElement?.key?.navTarget)) {
                this.elements.toSpotlightElements(elements.indexOf(currentActiveElement))
            } else {
                // if current navTarget does not exist in the new list of items and initialActiveIndex is null
                // then set 0 as active index
                this.elements.toSpotlightElements(0)
            }
        } else {
            this.elements.toSpotlightElements(initialActiveIndex)
        }
        
        return NavTransition(
            fromState = elements,
            targetState = state
        )
    }
}

fun <NavTarget : Any> InputSource<NavTarget, State>.updateElements(
    items: List<NavTarget>,
    initialActiveItem: Int? = null
) {
    operation(UpdateElements(items, initialActiveItem))
}

internal fun <NavTarget> List<NavTarget>.toSpotlightElements(activeIndex: Int): NavElements<NavTarget, State> =
    mapIndexed { index, item ->
        val state = when {
            index < activeIndex -> INACTIVE_BEFORE
            index == activeIndex -> ACTIVE
            else -> INACTIVE_AFTER
        }

        return@mapIndexed NavElement(
            key = NavKey(item),
            fromState = state,
            targetState = state,
            operation = Operation.Noop()
        )
    }
